/* This file is part of VoltDB.
 * Copyright (C) 2008-2015 VoltDB Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with VoltDB.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.voltdb.importclient.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.voltcore.logging.Level;
import org.voltdb.importer.AbstractImporter;
import org.voltdb.importer.CSVInvocation;
import org.voltdb.importer.ImporterConfig;

/**
 */
public class SocketServerImporter extends AbstractImporter {

    private SocketImporterConfig m_config;
    private List<ClientConnectionHandler> m_clients = new ArrayList<>();

    @Override
    protected ImporterConfig createImporterConfig()
    {
        m_config = new SocketImporterConfig();
        return m_config;
    }

    @Override
    public String getName()
    {
        return "SocketServerImporter";
    }

    @Override
    public void setBackPressure(boolean hasBackPressure)
    {
        for (ClientConnectionHandler client : m_clients) {
            client.hasBackPressure(hasBackPressure);
        }
    }

    @Override
    protected void readyForData(String resourceID)
    {
        SocketImporterConfig.InstanceConfiguration instanceConfig = m_config.getInstanceConfiguration(resourceID);
        //TODO: Make sure we don't need null check here. Merge 2 lines, if we don't need null check.
        startListening(instanceConfig);
    }

    @Override
    protected void stopImporter()
    {
        for (SocketImporterConfig.InstanceConfiguration aconfig : m_config.getAllInstanceConfigurations()) {
            try {
                aconfig.m_serverSocket.close();
            } catch(IOException e) {
                getLogger().warn("Error closing socket importer server socket on port " + aconfig.m_port, e);
            }
        }

        for (ClientConnectionHandler client : m_clients) {
            client.stopClient();
        }
    }

    private void startListening(SocketImporterConfig.InstanceConfiguration instanceConfig)
    {
        try {
            while (shouldRun()) {
                Socket clientSocket = instanceConfig.m_serverSocket.accept();
                ClientConnectionHandler ch = new ClientConnectionHandler(clientSocket, instanceConfig.m_procedure);
                m_clients.add(ch);
                ch.start();
            }
        } catch(IOException e) {
           getLogger().error("Unexpected error accepting client connections for " + getName() + " on port " + instanceConfig.m_port, e);
        }
    }

    //This is ClientConnection handler to read and dispatch data to stored procedure.
    private class ClientConnectionHandler extends Thread
    {
        private final Socket m_clientSocket;
        private final String m_procedure;
        private volatile boolean m_hasBackPressure;
        private volatile boolean m_isStopping;

        public ClientConnectionHandler(Socket clientSocket, String procedure)
        {
            m_clientSocket = clientSocket;
            m_procedure = procedure;
        }

        public void hasBackPressure(boolean flag)
        {
            m_hasBackPressure = flag;
        }

        @Override
        public void run()
        {
            try {
                while (!m_isStopping) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(m_clientSocket.getInputStream()));
                    while (true) {
                        String line = in.readLine();
                        //You should convert your data to params here.
                        if (line == null) break;
                        CSVInvocation invocation = new CSVInvocation(m_procedure, line);
                        if (!callProcedure(invocation)) {
                            rateLimitedLog(Level.ERROR, null, "Socket importer insertion failed");
                        }
                        if (m_hasBackPressure) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ioe) {
                                //
                            }
                        }
                    }
                    m_clientSocket.close();
                    getLogger().info("Client Closed.");
                }
            } catch (IOException ioe) {
                getLogger().error("IO exception reading from client socket connection in socket importer", ioe);
            }
        }

        public void stopClient()
        {
            m_isStopping = true;
        }
    }
}
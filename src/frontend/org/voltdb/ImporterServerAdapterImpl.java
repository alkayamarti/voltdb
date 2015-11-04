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

package org.voltdb;

import org.voltdb.client.ProcedureCallback;
import org.voltdb.importer.ImporterServerAdapter;
import org.voltdb.importer.ImporterStatsCollector;

/**
 */
public class ImporterServerAdapterImpl implements ImporterServerAdapter {
    private ImporterStatsCollector m_statsCollector;

    public ImporterServerAdapterImpl(ImporterStatsCollector statsCollector) {
        m_statsCollector = statsCollector;
    }
    /**
     * Returns true if a table with the given name exists in the server catalog.
     */
    public boolean hasTable(String name) {
        return VoltDB.instance().getClientInterface().getInternalConnectionHandler().hasTable(name);
    }

    @Override
    public boolean callProcedure(InternalConnectionContext ic, String proc, Object... fieldList) {
        return callProcedure(ic, null, proc, fieldList);
    }

    public boolean callProcedure(InternalConnectionContext ic, ProcedureCallback procCallback, String proc, Object... fieldList) {
        return getInternalConnectionHandler() //TODO: backpressure timeout is not used by internal connection handler. Remove it!
                .callProcedure(ic, m_statsCollector, 10, procCallback, proc, fieldList);
    }

    private InternalConnectionHandler getInternalConnectionHandler() {
        return VoltDB.instance().getClientInterface().getInternalConnectionHandler();
    }

    public void reportFailure(String importerName, String procName, boolean decrementPending) {
        m_statsCollector.reportFailure(importerName, procName, decrementPending);
    }

    public void reportQueued(String importerName, String procName) {
        m_statsCollector.reportQueued(importerName, procName);
    }
}
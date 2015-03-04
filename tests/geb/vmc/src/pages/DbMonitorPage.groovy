/* This file is part of VoltDB.
 * Copyright (C) 2008-2015 VoltDB Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package vmcTest.pages

import geb.navigator.Navigator
import java.io.*
import java.util.Date
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * This class represents the 'DB Monitor' tab of the VoltDB Management Center
 * (VMC) page, which is the VoltDB web UI (replacing the old Management Center).
 */
class DbMonitorPage extends VoltDBManagementCenterPage {
    static content = {
        activeIcon      { $('.activeIcon') }
        activeCount     { activeIcon.find('#activeCount') }
        missingIcon     { $('.missingIcon') }
        missingCount    { missingIcon.find('#missingCount') }
        alertIcon       (required: false) { $('.alertIcon') }
        alertCount      (required: false) { alertIcon.find('span') }
        joiningIcon     (required: false) { $('.joiningIcon') }
        joiningCount    (required: false) { joiningIcon.find('span') }
        serverButton    { $('#btnPopServerList') }
        serverList      { $('#popServerList') }
        servers         { serverList.find('.active') }
        servName        { servers.find('a') }
        servMemory      { servers.find('.memory-status') }
        showHideGraph   { $('#showHideGraphBlock') }
        graphsArea      { $('#graphChart') }
        showHideData    { $('#ShowHideBlock') }
        dataArea        { $('.menu_body') }

        serverCpu			{ $("#chartServerCPU") }
        serverRam			{ $("#chartServerRAM") }
        clusterLatency		{ $("#chartClusterLatency") }
        clusterTransactions	{ $("#chartClusterTransactions") }
        partitionIdleTime	{ $("#chartPartitionIdleTime") }
        storedProcedures 	{ $("#tblStoredProcedures") }
        dataTables			{ $("#tblDataTables") }

        serverCpuCheckbox				{ $("#ServerCPU") }
        serverRamCheckbox				{ $("#ServerRAM") }
        clusterLatencyCheckbox			{ $("#ClusterLatency") }
        clusterTransactionsCheckbox		{ $("#ClusterTransactions") }
        partitionIdleTimeCheckbox		{ $("#PartitionIdleTime") }
        storedProceduresCheckbox		{ $("#StoredProcedures") }
        dataTablesCheckbox				{ $("#DatabaseTables") }

        filterStoredProcedure			{ $("#filterStoredProc") }
        filterDatabaseTable				{ $("#filterDatabaseTable") }

        databaseTableCurrentPage		{ $("#lblPreviousTable") }
        databaseTableTotalPage		    { $("#lblTotalPagesofTables") }

        displayPreference           { $("#showMyPreference") }
        graphView					{ $('#graphView') }
        timeOne						{ $(class:"nv-axisMaxMin", transform:"translate(0,0)") }

        table			{ $("#TABLE_NAME") }
        rowcount		{ $("#TUPLE_COUNT") }
        maxrows			{ $("#MAX_ROWS") }
        minrows			{ $("#MIN_ROWS") }
        avgrows			{ $("#AVG_ROWS") }
        tabletype		{ $("#TABLE_TYPE") }

        ascending		{ $(class:"sorttable_sorted") }
        descending		{ $(class:"sorttable_sorted_reverse") }

        preferencesTitle		{ $(class:"overlay-title", text:"Graph/Data Preferences") }
        savePreferencesBtn		{ $("#savePreference") }
        popupClose				{ $(class:"popup_close") }
        header          		{ module Header }
        footer          		{ module Footer }
    }
    static at = {
        dbMonitorTab.displayed
        dbMonitorTab.attr('class') == 'active'
        showHideGraph.displayed
        showHideData.displayed
    }

    /**
     * Returns the count, as displayed (possibly in parentheses) in the
     * specified Navigator element; e.g., if "(3)" is displayed, then 3 is
     * returned (as an int).
     * <p>Note: if the specified Navigator element is not displayed on the
     * page, then -1 is returned; if the text in parentheses cannot be parsed
     * as an int, then -2 is returned.
     * @param nav - a Navigator specifying an element whose (parenthetical)
     * count is to be returned.
     * @return the displayed (parenthetical) count (or -1 if not shown, or
     * -2 if not parseable).
     */
    private int getCount(Navigator nav) {
        if (!nav.displayed) {
            return -1
        }
        String str = nav.text()
        try {
            return Integer.parseInt(str.replaceAll("[\\(\\)]", ""))
        } catch (NumberFormatException e) {
            System.err.println("Cannot parse '" + str + "' as an int.")
            e.printStackTrace()
            return -2
        }
    }

    /**
     * Returns the number of Active servers, as displayed in parentheses near
     * the top of the DB Monitor page; e.g., if "Active (3)" is displayed,
     * then 3 is returned (as an int).
     * <p>Note: if the Active server count is not displayed on the page, then
     * -1 is returned; if the text in parentheses cannot be parsed as an int,
     * then -2 is returned.
     * @return the number of Active servers, as shown on the page (or -1 if
     * not shown, or -2 if not parseable).
     */
    def int getActive() {
        return getCount(activeCount)
    }

    /**
     * Returns the number of Missing servers, as displayed in parentheses near
     * the top of the DB Monitor page; e.g., if "Missing (1)" is displayed,
     * then 1 is returned (as an int).
     * <p>Note: if the Missing server count is not displayed on the page, then
     * -1 is returned; if the text in parentheses cannot be parsed as an int,
     * then -2 is returned.
     * @return the number of Missing servers, as shown on the page (or -1 if
     * not shown, or -2 if not parseable).
     */
    def int getMissing() {
        return getCount(missingCount)
    }

    /**
     * Returns the number of server Alerts, as displayed in parentheses near
     * the top of the DB Monitor page; e.g., if "Alert (2)" is displayed,
     * then 2 is returned (as an int).
     * <p>Note: if the server Alert count is not displayed on the page, then
     * -1 is returned; if the text in parentheses cannot be parsed as an int,
     * then -2 is returned.
     * @return the number of server Alerts, as shown on the page (or -1 if
     * not shown, or -2 if not parseable).
     */
    def int getAlert() {
        return getCount(alertCount)
    }

    /**
     * Returns the number of Joining servers, as displayed in parentheses near
     * the top of the DB Monitor page; e.g., if "Joining (0)" is displayed,
     * then 0 is returned (as an int).
     * <p>Note: if the Joining server count is not displayed on the page, then
     * -1 is returned; if the text in parentheses cannot be parsed as an int,
     * then -2 is returned.
     * @return the number of Joining servers, as shown on the page (or -1 if
     * not shown, or -2 if not parseable).
     */
    def int getJoining() {
        return getCount(joiningCount)
    }

    /**
     * Returns true if the Server list is currently open (displayed).
     * @return true if the Server list is currently open (displayed).
     */
    def boolean isServerListOpen() {
        return serverList.displayed
    }

    /**
     * Opens the Server list, by clicking on the "Server" button (if it's not
     * open already).
     */
    def boolean openServerList() {
        clickToDisplay(serverButton, serverList)
        waitFor { servName.last().displayed }
        waitFor { servMemory.last().displayed }
        return true
    }

    /**
     * Closes the Server list, by clicking on the "Server" button (if it's not
     * closed already).
     */
    def boolean closeServerList() {
        clickToNotDisplay(serverButton, serverList)
        waitFor { !servName.first().displayed }
        waitFor { !servMemory.first().displayed }
        return true
    }

    /**
     * Returns the Server Names, as displayed on the Server list of the DB
     * Monitor page after clicking the "Server" button.
     * <p>Note: as a side effect, opens (if needed) and then closes the Server
     * Name list, by clicking the "Server" button.
     * @return the list of Server Names.
     */
    def List<String> getServerNames() {
        def names = []
        openServerList()
        servName.each { names.add(it.text()) }
        closeServerList()
        return names
    }
    
    /**
     * Returns the Memory Usage percentages, as displayed on the Server list
     * of the DB Monitor page after clicking the "Server" button - as Strings,
     * including the % signs.
     * <p>Note: as a side effect, opens (if needed) and then closes the Server
     * Name list, by clicking the "Server" button.
     * @return the list of Memory Usages (as Strings).
     */
    def List<String> getMemoryUsages() {
        def memUsages = []
        openServerList()
        servMemory.each { memUsages.add(it.text()) }
        closeServerList()
        return memUsages
    }

    /**
     * Returns the Memory Usage percentages, as displayed on the Server list
     * of the DB Monitor page after clicking the "Server" button - as Floats,
     * without the % signs.
     * <p>Note: as a side effect, opens (if needed) and then closes the Server
     * Name list, by clicking the "Server" button.
     * @return the list of Memory Usages (as Floats).
     */
    def List<Float> getMemoryUsagePercents() {
        def percents = []
        getMemoryUsages().each { percents.add(Float.parseFloat(it.replace("%", ""))) }
        return percents
    }

    /**
     * Returns the Memory Usage percentage of the specified server, as
     * displayed on the Server list of the DB Monitor page after clicking
     * the "Server" button - as a String, including the % sign.
     * <p>Note: as a side effect, opens (if needed) and then closes the Server
     * Name list, by clicking the "Server" button.
     * @param serverName - the name of the server whose Memory Usage is wanted.
     * @return the specified Memory Usage percentage (as String).
     */
    def String getMemoryUsage(String serverName) {
        openServerList()
        waitFor { servName.filter(text: serverName).next('.memory-status').displayed }
        String text = servName.filter(text: serverName).next('.memory-status').text()
        closeServerList()
        return text
    }
    
    /**
     * Returns the Memory Usage percentage of the specified server, as
     * displayed on the Server list of the DB Monitor page after clicking
     * the "Server" button - as a float, without the % signs.
     * <p>Note: as a side effect, opens (if needed) and then closes the Server
     * Name list, by clicking the "Server" button.
     * @param serverName - the name of the server whose Memory Usage is wanted.
     * @return the specified Memory Usage percentage (as float).
     */
    def float getMemoryUsagePercent(String serverName) {
        return Float.parseFloat(getMemoryUsage(serverName).replace("%", ""))
    }

    /**
     * Returns true if the "Graph" area (containing up to four graphs) is
     * currently open (displayed).
     * @return true if the "Graph" area is currently open.
     */
    def boolean isGraphAreaOpen() {
        return graphsArea.displayed
    }

    /**
     * Opens the "Graph" area (containing up to four graphs), by clicking on
     * "Show/Hide Graph" (if it's not open already).
     */
    def boolean openGraphArea() {
        clickToDisplay(showHideGraph, graphsArea)
        return true
    }

    /**
     * Closes the "Graph" area (containing up to four graphs), by clicking on
     * "Show/Hide Graph" (if it's not closed already).
     */
    def boolean closeGraphArea() {
        clickToNotDisplay(showHideGraph, graphsArea)
        return true
    }
    
    /**
     * Returns true if the "Data" area (containing Stored Procedures and
     * Database Tables info) is currently open (displayed).
     * @return true if the "Data" area is currently open.
     */
    def boolean isDataAreaOpen() {
        return dataArea.displayed
    }

    /**
     * Opens the "Data" area (containing Stored Procedures and Database Tables
     * info), by clicking on "Show/Hide Data" (if it's not open already).
     */
    def boolean openDataArea() {
        clickToDisplay(showHideData, dataArea)
        return true
    }

    /**
     * Closes the "Data" area (containing Stored Procedures and Database Tables
     * info), by clicking on "Show/Hide Data" (if it's not closed already).
     */
    def boolean closeDataArea() {
        clickToNotDisplay(showHideData, dataArea)
        return true
    }

	/**
	 *	Edits from here 
	 */

    /**
     * Check if preference button is displayed
     */
    def boolean displayPreferenceDisplayed() {
        return displayPreference.displayed
    }
	
	/**
     * Check if graph view button is displayed
     */
	def boolean graphViewDisplayed() {
		return graphView.displayed
	}

	/**
     * Check if Title of Preferences is displayed
     */
	def boolean preferencesTitleDisplayed() {
		return preferencesTitle.displayed
	}

	/**
     * Check if Save button of Preferences is displayed
     */
	def boolean savePreferencesBtnDisplayed() {
		return savePreferencesBtn.displayed
	}

	/**
     * Check if Popup Close is displayed
     */
	def boolean popupCloseDisplayed() {
		return popupClose.displayed
	}
	
	/**
     * Check if Server CPU is displayed
     */
	def boolean serverCpuDisplayed() {
		return serverCpu.displayed
	}
	
	/**
     * Check if Server RAM is displayed
     */
	def boolean serverRamDisplayed() {
		return serverRam.displayed
	}

	/**
     * Check if Cluster Latency is displayed
     */
	def boolean clusterLatencyDisplayed() {
		return clusterLatency.displayed
	}

	/**
     * Check if Cluster Transactions is displayed
     */
	def boolean clusterTransactionsDisplayed() {
		return clusterTransactions.displayed
	}

	/**
     * Check if Partition Idle Time is displayed
     */
	def boolean partitionIdleTimeDisplayed() {
		return partitionIdleTime.displayed
	}

	/**
     * Check if Stored Procedures is displayed
     */
	def boolean storedProceduresDisplayed() {
		return storedProcedures.displayed
	}

	/**
     * Check if Data Tables is displayed
     */
	def boolean dataTablesDisplayed() {
		return dataTables.displayed
	}

	/*
	 *	Returns true if Checkbox for Server CPU in preferences
	 */
	def boolean serverCpuCheckboxDisplayed() {
		return serverCpuCheckbox.displayed
	}

	/*
	 *	Returns true if Checkbox for Server RAM in preferences
	 */
	def boolean serverRamCheckboxDisplayed() {
		return serverRamCheckbox.displayed
	}

	/*
	 *	Returns true if Checkbox for Cluster Latency in preferences
	 */
	def boolean clusterLatencyCheckboxDisplayed() {
		return clusterLatencyCheckbox.displayed
	}

	/*
	 *	Returns true if Checkbox for Cluster Transactions in preferences
	 */
	def boolean clusterTransactionsCheckboxDisplayed() {
		return clusterTransactionsCheckbox.displayed
	}

	/*
	 *	Returns true if Checkbox for Partition Idle Time in preferences
	 */
	def boolean partitionIdleTimeCheckboxDisplayed() {
		return partitionIdleTimeCheckbox.displayed
	}

	/*
	 *	Returns true if Checkbox for Stored Procedures in preferences
	 */
	def boolean storedProceduresCheckboxDisplayed() {
		return storedProceduresCheckbox.displayed
	}

	/*
	 *	Returns true if Checkbox for Data Tables in preferences
	 */
	def boolean dataTablesCheckboxDisplayed() {
		return dataTablesCheckbox.displayed
	}

	/*
	 *	Returns true if display preferences is clicked
	 */
    def boolean openDisplayPreference() {
        displayPreference.click()
    }

	/*
	 *	Returns true if save button of preferences is clicked
	 */
	def boolean savePreferences() {
		savePreferencesBtn.click()
	}

	/*
	 *	Returns true if close popup button is clicked
	 */
	def boolean closePreferences() {
		popupClose.click()
	}

	/*
	 *	Click the check in Server CPU Checkbox
	 */
	def boolean serverCpuCheckboxClick() {
		serverCpuCheckbox.click()
	}

	/*
	 *	Click the check in Server RAM Checkbox
	 */
	def boolean serverRamCheckboxClick() {
		serverRamCheckbox.click()
	}

	/*
	 *	Click the check in Cluster Latency Checkbox
	 */
	def boolean clusterLatencyCheckboxClick() {
		clusterLatencyCheckbox.click()
	}
	
	/*
	 *	Click the check in Cluster Transactions Checkbox
	 */
	def boolean clusterTransactionsCheckboxClick() {
		clusterTransactionsCheckbox.click()
	}

	/*
	 *	Click the check in Partition Idle Time Checkbox
	 */
	def boolean partitionIdleTimeCheckboxClick() {
		partitionIdleTimeCheckbox.click()
	}

	/*
	 *	Click the check in Stored Procedures Checkbox
	 */
	def boolean storedProceduresCheckboxClick() {
		storedProceduresCheckbox.click()
	}

	/*
	 *	Click the check in Data Tables Checkbox
	 */
	def boolean dataTablesCheckboxClick() {
		dataTablesCheckbox.click()
	}

	def boolean chooseGraphView( String choice ) {
		graphView.value(choice)
	}

	def int changeToMinute( String string ) {
		String minute = string.substring(3, string.length()-3)
		int minuteInt = Integer.parseInt(minute)
		return minuteInt
	}

	def int changeToHour(String string) {
		String hour = string.substring(0, string.length()-6)
		int hourInt = Integer.parseInt(hour)
		return hourInt
	}

    /*
     * click SQL Query to go to SqlQueryPage
     */
    def boolean gotoSqlQuery() {
        header.tabSQLQuery.click()
    }

    /*
     * get query to create a table
     */
    def String getQueryToCreateTable() {
        BufferedReader br = new BufferedReader(new FileReader("src/resources/sqlQueryDbMonitor.txt"));
        String line;
        String query = ""

        while((line = br.readLine()) != "#create") {
        }

        while ((line = br.readLine()) != "#delete") {
            // process the line.
            query = query + line + "\n"
        }

        return query
    }

    /*
     * get query to delete a table
     */
    def String getQueryToDeleteTable() {
        BufferedReader br = new BufferedReader(new FileReader("src/resources/sqlQueryDbMonitor.txt"));
        String line;
        String query = ""

        while((line = br.readLine()) != "#delete") {
        }

        while ((line = br.readLine()) != null) {
            // process the line.
            query = query + line + "\n"
        }

        return query
    }


    /*
     *	search the tablename in Database Tables
     */
    def boolean searchDatabaseTable(String tablename) {
        filterDatabaseTable.value(tablename)
    }

    /*
     *	return true if table is in ascending order
     *  to check ascending order, check the class "sorttable_sorted" displayed
     */
    def boolean tableInAscendingOrder() {
        if ( ascending.displayed )
            return true
        else
            return false
    }

    /*
     *	return true if table is in ascending order
     *  to check ascending order, check the class "sorttable_sorted" displayed
     */
    def boolean tableInDescendingOrder() {
        if ( descending.displayed )
            return true
        else
            return false
    }

    /*
     *	click the table column in database table
     */
    def boolean clickTable() {
        table.click()
    }

    /*
     *	click the row count column in database table
     */
    def boolean clickRowcount() {
        rowcount.click()
    }

    /*
     *	click the max rows column in database table
     */
    def boolean clickMaxRows() {
        maxrows.click()
    }

    /*
     *	click the min rows column in database table
     */
    def boolean clickMinRows() {
        minrows.click()
    }

    /*
     *	click the avg rows column in database table
     */
    def boolean clickAvgRows() {
        avgrows.click()
    }

    /*
     *	click the type column in database table
     */
    def boolean clickTabletype() {
        tabletype.click()
    }
}
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

package vmcTest.tests

import vmcTest.pages.*
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class contains tests of the 'DB Monitor' tab of the VoltDB Management
 * Center (VMC) page, which is the VoltDB (new) web UI.
 */
class DbMonitorTest extends TestBase {

    def setup() { // called before each test
        // TestBase.setup gets called first (automatically)
        when: 'click the DB Monitor link (if needed)'
        page.openDbMonitorPage()
        then: 'should be on DB Monitor page'
        at DbMonitorPage
    }

    def 'confirm Graph area and Data area open initially'() {
        expect: 'Graph area open initially'
        page.isGraphAreaOpen()

        and: 'Data area open initially'
        page.isDataAreaOpen()
    }

    def openAndCloseGraphArea() {
        when: 'ensure the Graph area is open'
        if (!page.isGraphAreaOpen()) {
            page.openGraphArea()
        }
        then: 'Graph area is open (initially)'
        page.isGraphAreaOpen()

        when: 'click Show/Hide Graph (to close)'
        page.closeGraphArea()
        then: 'Graph area is closed'
        !page.isGraphAreaOpen()

        when: 'click Show/Hide Graph (to open)'
        page.openGraphArea()
        then: 'Graph area is open (again)'
        page.isGraphAreaOpen()

        when: 'click Show/Hide Graph (to close again)'
        page.closeGraphArea()
        then: 'Graph area is closed (again)'
        !page.isGraphAreaOpen()
    }

    def openAndCloseDataArea() {
        when: 'ensure the Data area is open'
        if (!page.isDataAreaOpen()) {
            page.openDataArea()
        }
        then: 'Data area is open (to start test)'
        page.isDataAreaOpen()

        when: 'click Show/Hide Data (to close)'
        page.closeDataArea()
        then: 'Data area is closed'
        !page.isDataAreaOpen()

        when: 'click Show/Hide Data (to open again)'
        page.openDataArea()
        then: 'Data area is open (again)'
        page.isDataAreaOpen()

        when: 'click Show/Hide Data (to close again)'
        page.closeDataArea()
        then: 'Data area is closed (again)'
        !page.isDataAreaOpen()
    }

    def checkActiveMissingJoining() {
        expect: '1 Active server (at least)'
        page.getActive() >= 1

        and: '0 Missing servers (initially)'
        page.getMissing() == 0

        and: 'Joining servers not shown (for now)'
        page.getJoining() == -1
    }

    def openAndCloseServerList() {
        expect: 'Server list closed initially'
        !page.isServerListOpen()
        
        when: 'click Server button (to open list)'
        page.openServerList()
        then: 'Server list is open'
        page.isServerListOpen()

        when: 'click Server button (to close list)'
        page.closeServerList()
        then: 'Server list is closed (again)'
        !page.isServerListOpen()
    }

    def triggerAlert() {
        expect: 'no Alerts shown, initially'
        page.getAlert() == -1

        // TODO: add more testing here, setting threshold
    }

    def checkServerNamesAndMemoryUsage() {
        expect: 'Server list closed initially'
        !page.isServerListOpen()

        // TODO: make this a real test, not just printing values
        List<String> serverNames = page.getServerNames()
        debugPrint "Server Names            : " + serverNames
        debugPrint "Memory Usages           : " + page.getMemoryUsages()
        debugPrint "Memory Usage Percents   : " + page.getMemoryUsagePercents()
        debugPrint "Memory Usage (0)        : " + page.getMemoryUsage(serverNames.get(0))
        debugPrint "Memory Usage Percent (0): " + page.getMemoryUsagePercent(serverNames.get(0))
    }

    // HEADER TESTS

	def "header banner exists" () {
		when:
			at DbMonitorPage
		then:
			header.banner.isPresent()
	}


    def "header image exists" () {
        when:
            at DbMonitorPage
        then:
            header.image.isDisplayed();
    }

    def "header username exists" () {
        when:
            at DbMonitorPage
        then:
            header.username.isDisplayed();
    }

    def "header logout exists" () {
        when:
            at DbMonitorPage
        then:
            header.logout.isDisplayed();
    }

    def "header help exists" () {
        when:
            at DbMonitorPage
        then:
            header.help.isDisplayed();
    }

    // HEADER TAB TESTS

    def "header tab dbmonitor exists" ()  {
        when:
            at DbMonitorPage
        then:
            header.tabDBMonitor.isDisplayed();
            header.tabDBMonitor.text().toLowerCase().equals("DB Monitor".toLowerCase())
    }

    def "header tab admin exists" ()  {
        when:
            at DbMonitorPage
        then:
            header.tabAdmin.isDisplayed();
            header.tabAdmin.text().toLowerCase().equals("Admin".toLowerCase())
    }

    def "header tab schema exists" ()  {
        when:
            at DbMonitorPage
        then:
            header.tabSchema.isDisplayed();
            header.tabSchema.text().toLowerCase().equals("Schema".toLowerCase())
    }

    def "header tab sql query exists" ()  {
        when:
            at DbMonitorPage
        then:
            header.tabSQLQuery.isDisplayed();
            header.tabSQLQuery.text().toLowerCase().equals("SQL Query".toLowerCase())
    }

    // USERNAME TEST

    def "header username check" () {
        def $line
        new File("src/pages/users.txt").withReader { $line = it.readLine() }

        when:
            at DbMonitorPage
        then:
            header.username.text().equals($line);
    }

    def "header username click and close" () {
        when:
            at DbMonitorPage
        then:
            header.username.click()
            header.logoutPopup.isDisplayed()
            header.logoutPopupTitle.isDisplayed()
            header.logoutPopupOkButton.isDisplayed()
            header.logoutPopupCancelButton.isDisplayed()
            header.popupClose.isDisplayed()
            header.popupClose.click()
    }

    def "header username click and cancel" () {
        when:
            at DbMonitorPage
        then:
            header.username.click()
            header.logoutPopup.isDisplayed()
            header.logoutPopupTitle.isDisplayed()
            header.logoutPopupOkButton.isDisplayed()
            header.logoutPopupCancelButton.isDisplayed()
            header.popupClose.isDisplayed()
            header.logoutPopupCancelButton.click()
    }


    // LOGOUT TEST

    def "logout button test close" ()  {
        when:
            at DbMonitorPage
        then:
            header.logout.click()
            header.logoutPopup.isDisplayed()
            header.logoutPopupTitle.isDisplayed()
            header.logoutPopupOkButton.isDisplayed()
            header.logoutPopupCancelButton.isDisplayed()
            header.popupClose.isDisplayed()
            header.popupClose.click()

    }

    def "logout button test cancel" ()  {
        when:
        at DbMonitorPage
        then:
        header.logout.click()
        header.logoutPopup.isDisplayed()
        header.logoutPopupTitle.isDisplayed()
        header.logoutPopupOkButton.isDisplayed()
        header.logoutPopupCancelButton.isDisplayed()
        header.popupClose.isDisplayed()
        header.logoutPopupCancelButton.click()

    }

    // HELP POPUP TEST

    def "help popup existance" () {
        when:
            at DbMonitorPage
            header.help.click()
        then:
            header.popup.isDisplayed()
            header.popupTitle.text().toLowerCase().equals("help".toLowerCase());
            header.popupClose.click()
    }

	// FOOTER TESTS

    def "footer exists" () {
        when:
            at DbMonitorPage
        then:
            footer.banner.isDisplayed();
    }

    def "footer text exists and valid"() {

        when:
            at DbMonitorPage
        then:
            footer.banner.isDisplayed();
            footer.text.text().toLowerCase().contains("VoltDB. All rights reserved.".toLowerCase());
    }

    def clickGraphViewSeconds() {
        expect: 'Graph view button exists'
        page.graphViewDisplayed()

        when: 'choose Seconds in Graph View'
        page.chooseGraphView("Seconds")
        then: 'display'

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String stringTwo = dateFormat.format(date)

        String stringOne = timeOne.text()
        int hourOne = page.changeToHour(stringOne)
        int minuteOne = page.changeToMinute(stringOne)

        int hourTwo = page.changeToHour(stringTwo)
        int minuteTwo = page.changeToMinute(stringTwo)

        int diff = minuteTwo - minuteOne

        if ( hourOne == hourTwo && diff < 20 ) {
            assert true
        }
        else if ( hourOne < hourTwo && minuteTwo < 20 ){
            assert true
        }
        else {
            assert false
        }
    }

    def clickGraphViewMinute() {
        expect: 'Graph view button exists'
        page.graphViewDisplayed()

        when: 'choose Seconds in Graph View'
        page.chooseGraphView("Minutes")
        then: 'display'

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String stringTwo = dateFormat.format(date)

        String stringOne = timeOne.text()
        int hourOne = page.changeToHour(stringOne)
        int minuteOne = page.changeToMinute(stringOne)

        int hourTwo = page.changeToHour(stringTwo)
        int minuteTwo = page.changeToHour(stringTwo)

        int hourDiff = hourTwo - hourOne

        if ( hourDiff == 1 ) {
            assert true
        }
        else if ( hourDiff > 1 && minuteTwo < 30 ){
            assert true
        }
        else {
            assert false
        }
    }

    def clickGraphViewDays() {
        expect: 'Graph view button exists'
        page.graphViewDisplayed()

        when: 'choose Seconds in Graph View'
        page.chooseGraphView("Days")
        then: 'display'

        String stringOne = timeOne.text()
        if ( stringOne.length() > 8 ) {
            assert true
        }
        else {
            assert false
        }
    }

    def "click display preferences and close"() {
        expect: 'Display Preference button exists'
        page.displayPreferenceDisplayed()

        when: 'click Display Preference button'
        page.openDisplayPreference()
        then: 'display title and save button of preferences'
        page.preferencesTitleDisplayed()
        page.savePreferencesBtnDisplayed()
        page.popupCloseDisplayed()

        when: 'click close button'
        page.closePreferences()
        then: 'all graph exist'
        page.serverCpuDisplayed()
        page.serverRamDisplayed()
        page.clusterLatencyDisplayed()
        page.clusterTransactionsDisplayed()
        page.partitionIdleTimeDisplayed()
    }

    def "click display preferences remove Server Cpu and again add Server Cpu"() {
        expect: 'Display Preference button exists'
        page.displayPreferenceDisplayed()

        when: 'click Display Preference button'
        page.openDisplayPreference()
        then: 'display title and save button of preferences'
        page.preferencesTitleDisplayed()
        page.savePreferencesBtnDisplayed()
        page.popupCloseDisplayed()

        when: 'Server CPU checkbox is displayed'
        page.serverCpuCheckboxDisplayed()
        then: 'Remove Server CPU'
        page.serverCpuCheckboxClick()

        when: 'click close button'
        page.savePreferences()
        then: 'no Server CPU displayed'
        !page.serverCpuDisplayed()
        page.serverRamDisplayed()
        page.clusterLatencyDisplayed()
        page.clusterTransactionsDisplayed()
        page.partitionIdleTimeDisplayed()

        when: 'click Display Preference button'
        page.openDisplayPreference()
        then: 'display title and save button of preferences'
        page.preferencesTitleDisplayed()
        page.savePreferencesBtnDisplayed()
        page.popupCloseDisplayed()

        when: 'Server CPU checkbox is displayed'
        page.serverCpuCheckboxDisplayed()
        then: 'Add Server CPU'
        page.serverCpuCheckboxClick()

        when: 'click close button'
        page.savePreferences()
        then: 'Server CPU displayed along with others'
        page.serverCpuDisplayed()
        page.serverRamDisplayed()
        page.clusterLatencyDisplayed()
        page.clusterTransactionsDisplayed()
        page.partitionIdleTimeDisplayed()
    }

    def "click display preferences remove Server RAM and again add Server RAM"() {
        expect: 'Display Preference button exists'
        page.displayPreferenceDisplayed()

        when: 'click Display Preference button'
        page.openDisplayPreference()
        then: 'display title and save button of preferences'
        page.preferencesTitleDisplayed()
        page.savePreferencesBtnDisplayed()
        page.popupCloseDisplayed()

        when: 'Server RAM checkbox is displayed'
        page.serverRamCheckboxDisplayed()
        then: 'Remove Server RAM'
        page.serverRamCheckboxClick()

        when: 'click close button'
        page.savePreferences()
        then: 'no Server RAM displayed'
        page.serverCpuDisplayed()
        !page.serverRamDisplayed()
        page.clusterLatencyDisplayed()
        page.clusterTransactionsDisplayed()
        page.partitionIdleTimeDisplayed()

        when: 'click Display Preference button'
        page.openDisplayPreference()
        then: 'display title and save button of preferences'
        page.preferencesTitleDisplayed()
        page.savePreferencesBtnDisplayed()
        page.popupCloseDisplayed()

        when: 'Server RAM checkbox is displayed'
        page.serverRamCheckboxDisplayed()
        then: 'Add Server RAM'
        page.serverRamCheckboxClick()

        when: 'click close button'
        page.savePreferences()
        then: 'Server RAM displayed along with others'
        page.serverCpuDisplayed()
        page.serverRamDisplayed()
        page.clusterLatencyDisplayed()
        page.clusterTransactionsDisplayed()
        page.partitionIdleTimeDisplayed()
    }


    def "click display preferences remove Cluster Latency and again add Cluster Latency"() {
        expect: 'Display Preference button exists'
        page.displayPreferenceDisplayed()

        when: 'click Display Preference button'
        page.openDisplayPreference()
        then: 'display title and save button of preferences'
        page.preferencesTitleDisplayed()
        page.savePreferencesBtnDisplayed()
        page.popupCloseDisplayed()

        when: 'Server Cluster Latency checkbox is displayed'
        page.clusterLatencyCheckboxDisplayed()
        then: 'Remove Cluster Latency'
        page.clusterLatencyCheckboxClick()

        when: 'click close button'
        page.savePreferences()
        then: 'no Cluster Latency displayed'
        page.serverCpuDisplayed()
        page.serverRamDisplayed()
        !page.clusterLatencyDisplayed()
        page.clusterTransactionsDisplayed()
        page.partitionIdleTimeDisplayed()

        when: 'click Display Preference button'
        page.openDisplayPreference()
        then: 'display title and save button of preferences'
        page.preferencesTitleDisplayed()
        page.savePreferencesBtnDisplayed()
        page.popupCloseDisplayed()

        when: 'Server Cluster Latency is displayed'
        page.clusterLatencyCheckboxDisplayed()
        then: 'Add Cluster Latency'
        page.clusterLatencyCheckboxClick()

        when: 'click close button'
        page.savePreferences()
        then: 'Cluster Latency displayed along with others'
        page.serverCpuDisplayed()
        page.serverRamDisplayed()
        page.clusterLatencyDisplayed()
        page.clusterTransactionsDisplayed()
        page.partitionIdleTimeDisplayed()
    }

    def "click display preferences remove Cluster Transactions and again add Cluster Transactions"() {
        expect: 'Display Preference button exists'
        page.displayPreferenceDisplayed()

        when: 'click Display Preference button'
        page.openDisplayPreference()
        then: 'display title and save button of preferences'
        page.preferencesTitleDisplayed()
        page.savePreferencesBtnDisplayed()
        page.popupCloseDisplayed()

        when: 'Server Cluster Transactions checkbox is displayed'
        page.clusterTransactionsCheckboxDisplayed()
        then: 'Remove Cluster Transactions'
        page.clusterTransactionsCheckboxClick()

        when: 'click close button'
        page.savePreferences()
        then: 'no Cluster Transactions displayed'
        page.serverCpuDisplayed()
        page.serverRamDisplayed()
        page.clusterLatencyDisplayed()
        !page.clusterTransactionsDisplayed()
        page.partitionIdleTimeDisplayed()

        when: 'click Display Preference button'
        page.openDisplayPreference()
        then: 'display title and save button of preferences'
        page.preferencesTitleDisplayed()
        page.savePreferencesBtnDisplayed()
        page.popupCloseDisplayed()

        when: 'Server Cluster Transactions is displayed'
        page.clusterTransactionsCheckboxDisplayed()
        then: 'Add Cluster Transactions'
        page.clusterTransactionsCheckboxClick()

        when: 'click close button'
        page.savePreferences()
        then: 'Cluster Transactions displayed along with others'
        page.serverCpuDisplayed()
        page.serverRamDisplayed()
        page.clusterLatencyDisplayed()
        page.clusterTransactionsDisplayed()
        page.partitionIdleTimeDisplayed()
    }

    def "click display preferences remove Partition Idle Time and again add Partition Idle Time"() {
        expect: 'Display Preference button exists'
        page.displayPreferenceDisplayed()

        when: 'click Display Preference button'
        page.openDisplayPreference()
        then: 'display title and save button of preferences'
        page.preferencesTitleDisplayed()
        page.savePreferencesBtnDisplayed()
        page.popupCloseDisplayed()

        when: 'Partition Idle Time checkbox is displayed'
        page.partitionIdleTimeCheckboxDisplayed()
        then: 'Remove Partition Idle Time'
        page.partitionIdleTimeCheckboxClick()

        when: 'click close button'
        page.savePreferences()
        then: 'no Partition Idle Time displayed'
        page.serverCpuDisplayed()
        page.serverRamDisplayed()
        page.clusterLatencyDisplayed()
        page.clusterTransactionsDisplayed()
        !page.partitionIdleTimeDisplayed()

        when: 'click Display Preference button'
        page.openDisplayPreference()
        then: 'display title and save button of preferences'
        page.preferencesTitleDisplayed()
        page.savePreferencesBtnDisplayed()
        page.popupCloseDisplayed()

        when: 'Partition Idle Time is displayed'
        page.partitionIdleTimeCheckboxDisplayed()
        then: 'Add Partition Idle Time'
        page.partitionIdleTimeCheckboxClick()

        when: 'click close button'
        page.savePreferences()
        then: 'Partition Idle Time displayed along with others'
        page.serverCpuDisplayed()
        page.serverRamDisplayed()
        page.clusterLatencyDisplayed()
        page.clusterTransactionsDisplayed()
        page.partitionIdleTimeDisplayed()
    }



    def "click display preferences remove Stored Procedures and again add Stored Procedures"() {
        expect: 'Display Preference button exists'
        page.displayPreferenceDisplayed()

        when: 'click Display Preference button'
        page.openDisplayPreference()
        then: 'display title and save button of preferences'
        page.preferencesTitleDisplayed()
        page.savePreferencesBtnDisplayed()
        page.popupCloseDisplayed()

        when: 'Stored Procedures checkbox is displayed'
        page.storedProceduresCheckboxDisplayed()
        then: 'Remove Stored Procedures'
        page.storedProceduresCheckboxClick()

        when: 'click close button'
        page.savePreferences()
        then: 'no Stored Procedures displayed'
        page.serverCpuDisplayed()
        page.serverRamDisplayed()
        page.clusterLatencyDisplayed()
        page.clusterTransactionsDisplayed()
        page.partitionIdleTimeDisplayed()
        !page.storedProceduresDisplayed()
        page.dataTablesDisplayed()

        when: 'click Display Preference button'
        page.openDisplayPreference()
        then: 'display title and save button of preferences'
        page.preferencesTitleDisplayed()
        page.savePreferencesBtnDisplayed()
        page.popupCloseDisplayed()

        when: 'Stored Procedures is displayed'
        page.storedProceduresCheckboxDisplayed()
        then: 'Add Stored Procedures'
        page.storedProceduresCheckboxClick()

        when: 'click close button'
        page.savePreferences()
        then: 'Stored Procedures displayed along with others'
        page.serverCpuDisplayed()
        page.serverRamDisplayed()
        page.clusterLatencyDisplayed()
        page.clusterTransactionsDisplayed()
        page.partitionIdleTimeDisplayed()
        page.storedProceduresDisplayed()
        page.dataTablesDisplayed()
    }

    def "click display preferences remove Data Tables and again add Data Tables"() {
        expect: 'Display Preference button exists'
        page.displayPreferenceDisplayed()

        when: 'click Display Preference button'
        page.openDisplayPreference()
        then: 'display title and save button of preferences'
        page.preferencesTitleDisplayed()
        page.savePreferencesBtnDisplayed()
        page.popupCloseDisplayed()

        when: 'Data Tables checkbox is displayed'
        page.dataTablesCheckboxDisplayed()
        then: 'Remove Data Tables'
        page.dataTablesCheckboxClick()

        when: 'click close button'
        page.savePreferences()
        then: 'no Data Tables displayed'
        page.serverCpuDisplayed()
        page.serverRamDisplayed()
        page.clusterLatencyDisplayed()
        page.clusterTransactionsDisplayed()
        page.partitionIdleTimeDisplayed()
        page.storedProceduresDisplayed()
        !page.dataTablesDisplayed()

        when: 'click Display Preference button'
        page.openDisplayPreference()
        then: 'display title and save button of preferences'
        page.preferencesTitleDisplayed()
        page.savePreferencesBtnDisplayed()
        page.popupCloseDisplayed()

        when: 'Data Tables is displayed'
        page.dataTablesCheckboxDisplayed()
        then: 'Add Data Tables'
        page.dataTablesCheckboxClick()

        when: 'click close button'
        page.savePreferences()
        then: 'Data Tables displayed along with others'
        page.serverCpuDisplayed()
        page.serverRamDisplayed()
        page.clusterLatencyDisplayed()
        page.clusterTransactionsDisplayed()
        page.partitionIdleTimeDisplayed()
        page.storedProceduresDisplayed()
        page.dataTablesDisplayed()
    }


    def "Add a table in Tables and check it"() {
        String createQuery = page.getQueryToCreateTable()
        String deleteQuery = page.getQueryToDeleteTable()

        when: 'sql query tab is clicked'
        page.gotoSqlQuery()
        then: 'at sql query'
        at SqlQueryPage

        when: 'set query in the box'
        page.setQueryText(createQuery)
        then: 'run the query'
        page.runQuery()

        when: 'Db Monitor tab is clicked'
        page.gotoDbMonitor()
        then: 'at DbMonitor Page'
        at DbMonitorPage

        when:
        page.searchDatabaseTable("lina")
        then:
        String number = page.databaseTableCurrentPage.text()
        String numbers = page.databaseTableTotalPage.text()

        when: 'sql query tab is clicked'
        page.gotoSqlQuery()
        then: 'at sql query'
        at SqlQueryPage

        when: 'set query in the box'
        page.setQueryText(deleteQuery)
        then: 'run the query'
        page.runQuery()

        when: 'Db Monitor tab is clicked'
        page.gotoDbMonitor()
        then: 'at DbMonitor Page'
        at DbMonitorPage

        when:
        page.searchDatabaseTable("lina")
        then:
        String number1 = page.databaseTableCurrentPage.text()
        String numbers1 = page.databaseTableTotalPage.text()

        !number.equals("0")
        !numbers.equals("0")
        number1.equals("0")
        numbers1.equals("0")
    }
}
package edu.sdstate.eastweb.prototype.download.cache.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.download.cache.DateCache;
import edu.sdstate.eastweb.prototype.download.cache.NldasCache;
import edu.sdstate.eastweb.prototype.tests.DataDateTests;

public class NldasCahceTest {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        File temp=new File("c://test.xml");
        DataDate lastUpdated=new DataDate(11,12,2013);
        DataDate startDate=new DataDate(1,12,2013);
        List<DataDate> dates = DataDateTests.randomList();
        dates.add(DataDate.DataDateWithHour(1, 1, 2013));
        dates.add(DataDate.DataDateWithHour(1, 2, 2013));
        final NldasCache ref = new NldasCache(lastUpdated, startDate, dates);
        ref.toFile(temp);


    }
}


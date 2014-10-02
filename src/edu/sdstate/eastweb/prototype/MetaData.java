package edu.sdstate.eastweb.prototype;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class MetaData {

    public DownloadMetaData Download;
    public ProjectionMetaData Projection;
    public SummaryMetaData Summary;
    public String IndicesMetaData;
    public String Title;

    public MetaData() throws ParserConfigurationException, SAXException, IOException{

        File fXmlFile = new File(System.getProperty("user.dir") + "\\metaData.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        doc.getDocumentElement().normalize();

        Title = doc.getElementsByTagName("title").item(0).getTextContent();
        Download = new DownloadMetaData(doc.getElementsByTagName("Download"));
        Projection = new ProjectionMetaData(doc.getElementsByTagName("Projection"));
        IndicesMetaData = doc.getElementsByTagName("Indices").item(0).getTextContent();
        Summary = new SummaryMetaData();
    }

    private static MetaData instance;

    public static MetaData GetInstance() throws ParserConfigurationException, SAXException, IOException
    {
        if(instance == null) {
            instance = new MetaData();
        }

        return instance;
    }


    public class DownloadMetaData{
        private NodeList nList;

        public String ftpType;
        public String ftphostName;
        public String ftpNbarRootDir;
        public String ftpUserName;
        public String ftpPassword;
        public String downloadClassname;

        public DownloadMetaData(NodeList n){
            nList = n;
            Node downloadNode = nList.item(0);
            Node ftpNode = ((Element) downloadNode).getElementsByTagName("Ftp").item(0);

            downloadClassname = ((Element) downloadNode).getElementsByTagName("className").item(0).getTextContent();
            ftpType = ((Element) ftpNode).getElementsByTagName("type").item(0).getTextContent();
            ftphostName   = ((Element) ftpNode).getElementsByTagName("hostName").item(0).getTextContent();
            ftpNbarRootDir  = ((Element) ftpNode).getElementsByTagName("nbarRootDir").item(0).getTextContent();
            ftpUserName  = ((Element) ftpNode).getElementsByTagName("userName").item(0).getTextContent();
            ftpPassword  = ((Element) ftpNode).getElementsByTagName("passWord").item(0).getTextContent();
        }

    }
    public class ProjectionMetaData {

        private NodeList nList;

        public String projectionClassName;
        public Boolean projectionMozaix;
        public String convertHasConvert;
        public String convertOriFormat;
        public String convertToFormat;
        public String convertGeoTransform;
        public String convertProjectionStr;
        public String filterClassName;
        public Boolean filterRunFilter;

        public ProjectionMetaData(NodeList n){
            nList = n;
            Node projectionNode = nList.item(0);

            projectionClassName = ((Element) projectionNode).getElementsByTagName("className").item(0).getTextContent();
            projectionMozaix = Boolean.valueOf(((Element) projectionNode).getElementsByTagName("mozaic").item(0).getTextContent());

            Node convertNode = ((Element) projectionNode).getElementsByTagName("convert").item(0);
            convertHasConvert = ((Element) convertNode).getElementsByTagName("hasConvert").item(0).getTextContent();
            convertOriFormat = ((Element) convertNode).getElementsByTagName("oriFormat").item(0).getTextContent();
            convertToFormat = ((Element) convertNode).getElementsByTagName("toFormat").item(0).getTextContent();
            convertGeoTransform = ((Element) convertNode).getElementsByTagName("GeoTransform").item(0).getTextContent();
            convertProjectionStr = ((Element) convertNode).getElementsByTagName("projectionStr").item(0).getTextContent();

            Node filterNode = ((Element) projectionNode).getElementsByTagName("filter").item(0);
            filterClassName = ((Element) filterNode).getElementsByTagName("className").item(0).getTextContent();
            filterRunFilter = Boolean.valueOf(((Element) filterNode).getElementsByTagName("runFilter").item(0).getTextContent());
        }
    }

    public class SummaryMetaData{

    }


}


package version2.prototype;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class PluginMetaDataCollection {
    public static Map<String,PluginMetaData> instance;
    public static ArrayList<String> pluginList;


    public PluginMetaDataCollection(ArrayList<String> listOfPluginMetaData) throws ParserConfigurationException, SAXException, IOException{

        pluginList= listOfPluginMetaData;
    }

    Map<String, PluginMetaData> createMap(ArrayList<String> pluginList) throws ParserConfigurationException, SAXException, IOException{
        Map<String,PluginMetaData> myMap=new HashMap<String,PluginMetaData>();
        for(String item: pluginList){
            File fXmlFile = new File(System.getProperty("user.dir") + "\\Plugin_"+item+".xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();
            PluginMetaData temp=new PluginMetaData();
            temp.Title = doc.getElementsByTagName("title").item(0).getTextContent();
            temp.Download = new DownloadMetaData(doc.getElementsByTagName("Download"));
            temp.Projection = new ProjectionMetaData(doc.getElementsByTagName("Projection"));
            temp.IndicesMetaData = doc.getElementsByTagName("Indices").item(0).getTextContent();
            temp.Summary = new SummaryMetaData();
            myMap.put(item, temp);
        }
        return myMap;
    }

    public class PluginMetaData {

        public DownloadMetaData Download;
        public ProjectionMetaData Projection;
        public SummaryMetaData Summary;
        public String IndicesMetaData;
        public String Title;
    }


    public class DownloadMetaData{
        private NodeList nList;
        public String mode;// the protocol type: ftp or http
        public ftp myFtp;
        public http myHttp;

        public DownloadMetaData(NodeList n){
            myFtp=null;
            myHttp=null;
            nList = n;
            Node downloadNode = nList.item(0);
            mode=((Element) downloadNode).getElementsByTagName("type").item(0).getTextContent();
            mode=mode.toUpperCase();
            if(mode.equalsIgnoreCase("Ftp")){
                myFtp=new ftp(((Element)downloadNode).getElementsByTagName(mode).item(0));
            }else{
                myHttp=new http(((Element)downloadNode).getElementsByTagName(mode).item(0));
            }
        }
    }

    public class ftp {
        public String hostName;
        public String rootDir;
        public String userName;
        public String password;

        public ftp(Node e){
            hostName=((Element)e).getElementsByTagName("hostName").item(0).getTextContent();
            rootDir=((Element)e).getElementsByTagName("rootDir").item(0).getTextContent();
            userName=((Element)e).getElementsByTagName("userName").item(0).getTextContent();
            password=((Element)e).getElementsByTagName("passWord").item(0).getTextContent();
        }
    }

    public class http {
        public String url;
        public http(Node e){
            url=((Element)e).getElementsByTagName("url").item(0).getTextContent();
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


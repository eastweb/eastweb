package edu.sdstate.eastweb.prototype;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import edu.sdstate.eastweb.prototype.Projection;
import edu.sdstate.eastweb.prototype.Projection.*;
import edu.sdstate.eastweb.prototype.download.Downloader.Mode;
import edu.sdstate.eastweb.prototype.util.*;

public class Config {
    private static final String PROJECTS_DIR = ".\\projects";
    private static final String CONFIG_FILENAME = ".\\config.xml";
    private static final String ROOT_DIRECTORY_KEY = "ROOT_DIRECTORY";
    private static final String TEMP_DIRECTORY_KEY = "TEMP_DIRECTORY";
    private static final String PRODUCT_LIST_KEY="productList";
    private static final String MODIS_KEY = "MODIS";
    private static final String HOST_NAME_KEY = "hostName";
    private static final String ROOT_DIR_KEY = "rootDir";
    private static final String URL_KEY = "url";
    private static final String TYPE_KEY = "type";
    private static final String USER_NAME_KEY = "userName";
    private static final String PASS_WORD_KEY = "passWord";
    private static final String TRMM_3B42_ROOT_DIR_KEY = "Trmm3B42RootDir";
    private static final String TRMM_3B42RT_ROOT_DIR_KEY = "Trmm3B42RTRootDir";
    private static final String MODIS_NBAR_URL_KEY="nbarUrl";
    private static final String MODIS_LST_URL_KEY="lstUrl";
    private static final String MODIS_NBAR_ROOT_DIR_KEY = "nbarRootDir";
    private static final String MODIS_LST_ROOT_DIR_KEY = "lstRootDir";
    private static final String ETO_KEY = "Eto";
    private static final String TRMM_KEY = "Trmm";
    private static final String DATA_BASE_KEY = "DataBase";
    private static final String DATABASE_USERNAME_KEY = "userName";
    private static final String DATABASE_PASSWORD_KEY = "passWord";
    private static final String DOWNLOAD_REFRESH_DAYS_KEY ="DOWNLOAD_REFRESH_DAYS";
    private static final String NAD83_NAD27_TRANSFORM_KEY = "NAD83_NAD27_TRANSFORM";
    private static final String NAD83_WGS72_TRANSFORM_KEY = "NAD83_WGS72_TRANSFORM";
    private static final String NAD83_WGS84_TRANSFORM_KEY = "NAD83_WGS84_TRANSFORM";
    private static final String WGS84_NAD27_TRANSFORM_KEY = "WGS84_NAD27_TRANSFORM";
    private static final String WGS84_WGS72_TRANSFORM_KEY = "WGS84_WGS72_TRANSFORM";
    //private static final String DOWNLOAD_REFRESH_DAYS_KEY = "DOWNLOAD_REFRESH_DAYS";
    private static final String TRANSFORM_KEY = "Transform";
    private static final String HOST_ADDRESS_KEY = "HOST_ADDRESS";
    private static final String CONTROL_PORT_KEY = "CONTROL_PORT";
    private static final String TRANSFER_PORT_KEY = "TRANSFER_PORT";

    private static final LazyCachedReference<Config, ConfigReadException> instance =
        new LazyCachedReference<Config, ConfigReadException>() {
        @Override
        protected Config makeInstance() throws ConfigReadException {
            try {
                return new Config();
            } catch (ConfigReadException e) {
                throw e;
            } catch (Exception e) {
                throw new ConfigReadException(e);
            }
        }
    };

    private String rootDirectory;
    private String tempDirectory;
    private String modisHostName;
    private String modisNbarRootDir;
    private String modisLstRootDir;
    private String modisUserName;
    private String modisPassWord;
    private String modisNbarUrl;
    private String modisLstUrl;
    private String etoHttpUrl;
    private String etoFtpHostName;
    private String etoRootDir;
    private String etoUserName;
    private String etoPassWord;
    private String trmmFtpHostName;
    private String trmmRootDir3B42;
    private String trmmRootDir3B42RT;
    private String trmmUserName;
    private String trmmPassWord;
    private String trmmHttpUrl;
    private String mrtDir;
    private String databaseHost;
    private String databaseUsername;
    private String databasePassword;
    private int downloadRefreshDays = -1;
    private String hostAddress;
    private int controlPort = -1;
    private int transferPort = -1;
    private Mode modisDownMode;
    private Mode etoDownMode;
    private Mode trmmDownMode;
    private String[] productList;

    //<auto generate variables>
    //add new private variable for TRMM3B42
    private String TRMM_3B42FtpHostName;
    private String TRMM_3B42RootDir;
    private String TRMM_3B42UserName;
    private String TRMM_3B42PassWord;
    private String TRMM_3B42Url;
    private Mode TRMM_3B42DownMode;

    //add new private variable for NLDAS
    private String NLDASFtpHostName;
    private String NLDASRootDir;
    private String NLDASUserName;
    private String NLDASPassWord;
    private String NLDASUrl;
    private Mode NLDASDownMode;

    //<auto generate variables>

    private Hashtable<Datum, String> trmmTransforms;
    private Hashtable<Datum, String> etoTransforms;


    private Config() throws Exception {
        trmmTransforms = new Hashtable<Datum, String>();
        etoTransforms = new Hashtable<Datum, String>();

        trmmTransforms.put(Datum.WGS84, "#");
        etoTransforms.put(Datum.NAD83, "#");

        loadSettings();
    }

    public static Config getInstance() throws ConfigReadException {
        return instance.get();
    }

    /**
     * Parses config file and loads settings.
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws Exception
     */
    private void loadSettings() throws ConfigReadException, IOException, SAXException, ParserConfigurationException {
        File fXmlFile = new File(CONFIG_FILENAME);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        //root.setIdAttribute("id", true);

        rootDirectory=doc.getElementsByTagName(ROOT_DIRECTORY_KEY).item(0).getTextContent();
        tempDirectory=doc.getElementsByTagName(TEMP_DIRECTORY_KEY).item(0).getTextContent();
        // FIXME: not really in the right place
        File file = new File(rootDirectory);
        if (!file.exists()) {
            FileUtils.forceMkdir(file);
        }


        String pList=doc.getElementsByTagName(PRODUCT_LIST_KEY).item(0).getTextContent();
        setProductList(pList.split(";"));

        Element modis = (Element) doc.getElementsByTagName(MODIS_KEY).item(0);
        String modisMode=modis.getElementsByTagName(TYPE_KEY).item(0).getTextContent();
        if(modisMode.equals("http")){
            setModisDownMode(Mode.HTTP);
            //TODO: replace hostname and rootdir with real url
            //modisHttpUrl=modis.getElementsByTagName(URL_KEY).item(0).getTextContent();
            modisLstUrl=modis.getElementsByTagName(MODIS_LST_URL_KEY).item(0).getTextContent();
            modisNbarUrl=modis.getElementsByTagName(MODIS_NBAR_URL_KEY).item(0).getTextContent();
        }else if(modisMode.equals("ftp")){
            setModisDownMode(Mode.FTP);
            modisHostName=modis.getElementsByTagName(HOST_NAME_KEY).item(0).getTextContent();
            modisNbarRootDir=modis.getElementsByTagName(MODIS_NBAR_ROOT_DIR_KEY).item(0).getTextContent();
            modisLstRootDir=modis.getElementsByTagName(MODIS_LST_ROOT_DIR_KEY).item(0).getTextContent();
            setModisUserName(modis.getElementsByTagName(USER_NAME_KEY).item(0).getTextContent());
            setModisPassWord(modis.getElementsByTagName(PASS_WORD_KEY).item(0).getTextContent());
        }


        Element eto=(Element) doc.getElementsByTagName(ETO_KEY).item(0);
        String etoMode=eto.getElementsByTagName(TYPE_KEY).item(0).getTextContent();
        if(etoMode.equals("http")){
            setEtoDownMode(Mode.HTTP);
            etoHttpUrl=eto.getElementsByTagName(URL_KEY).item(0).getTextContent();
        }else if(etoMode.equals("ftp")){
            setEtoDownMode(Mode.FTP);
            setEtoFtpHostName(eto.getElementsByTagName(HOST_NAME_KEY).item(0).getTextContent());
            setEtoRootDir(eto.getElementsByTagName(ROOT_DIR_KEY).item(0).getTextContent());
            setEtoUserName(eto.getElementsByTagName(USER_NAME_KEY).item(0).getTextContent());
            setEtoPassWord(eto.getElementsByTagName(PASS_WORD_KEY).item(0).getTextContent());
        }

        Element trmm=(Element) doc.getElementsByTagName(TRMM_KEY).item(0);
        String trmmMode=trmm.getElementsByTagName(TYPE_KEY).item(0).getTextContent();
        if(trmmMode.equals("http")){
            setTrmmDownMode(Mode.HTTP);
            trmmHttpUrl=trmm.getElementsByTagName(URL_KEY).item(0).getTextContent();
        }else if(trmmMode.equals("ftp")){
            setTrmmDownMode(Mode.FTP);
            trmmFtpHostName=trmm.getElementsByTagName(HOST_NAME_KEY).item(0).getTextContent();
            trmmRootDir3B42=trmm.getElementsByTagName(TRMM_3B42_ROOT_DIR_KEY).item(0).getTextContent();
            trmmRootDir3B42RT=trmm.getElementsByTagName(TRMM_3B42RT_ROOT_DIR_KEY).item(0).getTextContent();
            setTrmmUserName(trmm.getElementsByTagName(USER_NAME_KEY).item(0).getTextContent());
            setTrmmPassWord(trmm.getElementsByTagName(PASS_WORD_KEY).item(0).getTextContent());
        }

        //<auto generate code>
        //read setting data into TRMM3B42
        Element TRMM_3B42=(Element) doc.getElementsByTagName("TRMM_3B42").item(0);
        String TRMM_3B42Mode=TRMM_3B42.getElementsByTagName(TYPE_KEY).item(0).getTextContent();
        if(TRMM_3B42Mode.equals("http")){
            setTRMM_3B42DownMode(Mode.HTTP);
            trmmHttpUrl=TRMM_3B42.getElementsByTagName(URL_KEY).item(0).getTextContent();
        }else if(TRMM_3B42Mode.equals("ftp")){
            setTRMM_3B42DownMode(Mode.FTP);
            setTRMM_3B42FtpHostName(TRMM_3B42.getElementsByTagName(HOST_NAME_KEY).item(0).getTextContent());
            setTRMM_3B42RootDir(TRMM_3B42.getElementsByTagName(ROOT_DIR_KEY).item(0).getTextContent());
            setTRMM_3B42UserName(TRMM_3B42.getElementsByTagName(USER_NAME_KEY).item(0).getTextContent());
            setTRMM_3B42PassWord(TRMM_3B42.getElementsByTagName(PASS_WORD_KEY).item(0).getTextContent());
        }

        //read setting data into nldas
        Element NLDAS=(Element) doc.getElementsByTagName("NLDAS" ).item(0);
        String NLDASMode=NLDAS.getElementsByTagName( TYPE_KEY).item(0).getTextContent();
        if(NLDASMode.equals( "http")){
            setNLDASDownMode(Mode. HTTP);
            trmmHttpUrl=NLDAS.getElementsByTagName(URL_KEY).item(0).getTextContent();
        } else if (NLDASMode .equals("ftp" )){
            setNLDASDownMode(Mode. FTP);
            setNLDASFtpHostName(NLDAS.getElementsByTagName(HOST_NAME_KEY).item(0).getTextContent());
            setNLDASRootDir(NLDAS.getElementsByTagName(ROOT_DIR_KEY).item(0).getTextContent());
            setNLDASUserName(NLDAS.getElementsByTagName(USER_NAME_KEY).item(0).getTextContent());
            setNLDASPassWord(NLDAS.getElementsByTagName(PASS_WORD_KEY).item(0).getTextContent());
        }

        //<auto generate code>


        Element database=(Element) doc.getElementsByTagName(DATA_BASE_KEY).item(0);
        databaseHost="jdbc:postgresql://"+database.getElementsByTagName(HOST_NAME_KEY).item(0).getTextContent();
        databaseUsername=database.getElementsByTagName(DATABASE_USERNAME_KEY).item(0).getTextContent();
        databasePassword=database.getElementsByTagName(DATABASE_PASSWORD_KEY).item(0).getTextContent();

        Element dfrDays=(Element) doc.getElementsByTagName(DOWNLOAD_REFRESH_DAYS_KEY).item(0);
        downloadRefreshDays=Integer.parseInt(dfrDays.getTextContent());

        NodeList transform=doc.getElementsByTagName(TRANSFORM_KEY);
        for (int count = 0; count < transform.getLength(); count++){
            Node temp=transform.item(count);
            String name=temp.getNodeName();
            String value=temp.getTextContent();
            if(name.equals(NAD83_NAD27_TRANSFORM_KEY)){
                etoTransforms.put(Datum.NAD27, value);
            }else if (name.equals(NAD83_WGS72_TRANSFORM_KEY)) {
                etoTransforms.put(Datum.WGS72, value);
            } else if (name.equals(NAD83_WGS84_TRANSFORM_KEY)) {
                etoTransforms.put(Datum.WGS84, value);
                trmmTransforms.put(Datum.NAD83, value);
            } else if (name.equals(WGS84_NAD27_TRANSFORM_KEY)) {
                trmmTransforms.put(Datum.NAD27, value);
            } else if (name.equals(WGS84_WGS72_TRANSFORM_KEY)) {
                trmmTransforms.put(Datum.WGS72, value);
            }
        }

        //TODO: check if trmm and eto transforms are specified?
        if (rootDirectory == null ||tempDirectory==null||
                databaseHost == null ||databasePassword == null || databaseUsername == null ||
                downloadRefreshDays == -1
        ){
            throw new ConfigReadException("Not all configuration parameters provided!");
        }else if( etoHttpUrl == null&&(etoFtpHostName==null||etoRootDir==null)) {
            throw new ConfigReadException("Not all eto download configuration parameters provided!");
        }else if( trmmHttpUrl == null&&(trmmFtpHostName==null||trmmRootDir3B42==null||trmmRootDir3B42RT==null)) {
            throw new ConfigReadException("Not all trmm download configuration parameters provided!");
        }else if( (modisLstUrl == null||modisNbarUrl == null)&&(modisHostName == null||modisNbarRootDir==null||modisLstRootDir==null)) {
            throw new ConfigReadException("Not all trmm download configuration parameters provided!");
        }


    }

    /**
     * Normalizes a project name to a file system friendly equivalent.
     * 
     * @param name name to normalize
     * @return normalized name
     */
    public String normalizeName(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "_");
    }


    /**
     * @param name normalized or non-normalized project name
     * @return full file path
     */
    public File getProjectFilename(String name) {
        return new File(PROJECTS_DIR, normalizeName(name) + ".xml");
    }


    /**
     * @return list of normalized project names
     * @throws Exception
     */
    public String[] getProjectNames() throws Exception {
        File projectsFolder = new File(PROJECTS_DIR);

        Vector<String> projects = new Vector<String>();

        for (String filename : projectsFolder.list()) {
            // Skip files that don't end in ".xml" to make testing easier
            if (!new File(filename).getName().endsWith(".xml")) {
                continue;
            }

            try {
                Document document = XmlUtils.parse(
                        new File(PROJECTS_DIR, filename));

                Node project = document.getElementsByTagName("project").item(0);

                NodeList projectNodes = project.getChildNodes();
                for (int i=0; i<projectNodes.getLength(); i++) {
                    Node node = projectNodes.item(i);
                    String nodeName = node.getNodeName();

                    if (nodeName.equals("name")) {
                        projects.add(node.getTextContent());
                    }
                }
            } catch (IOException  e) {
                // Ignore
            }
        }

        return projects.toArray(new String[0]);
    }

    public ProjectInfo[] getProjects() throws Exception {
        String projectNames[] = getProjectNames();
        ProjectInfo[] projects = new ProjectInfo[projectNames.length];
        for (int i=0; i<projects.length; i++) {
            projects[i] = loadProject(projectNames[i]);
        }

        return projects;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public String getModisFtpHostName() {
        return modisHostName;
    }

    public String getModisNbarRootDir() {
        return modisNbarRootDir;
    }

    public String getModisLstRootDir() {
        return modisLstRootDir;
    }

    public String getEtoHttpUrl() {
        return etoHttpUrl;
    }

    public String getTrmmFtpHostName() {
        return trmmFtpHostName;
    }

    public String getTrmm3b42RootDir() {
        return trmmRootDir3B42;
    }

    public String getTrmm3b42rtRootDir() {
        return trmmRootDir3B42RT;
    }

    public String getTrmmHttpUrl() {
        return trmmHttpUrl;
    }

    public String getMRTDir() {
        return mrtDir;
    }

    public String getMosiacPath() {
        return new File(mrtDir, "bin//mrtmosaic.exe").getAbsolutePath();
    }

    public String getResamplerPath() {
        return new File(mrtDir, "bin//resample.exe").getAbsolutePath();
    }

    public String getDatabaseHost() {
        return databaseHost;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public String getTrmmTransform(Datum datum) {
        return trmmTransforms.get(datum);
    }

    public String getEToTransform(Datum datum) {
        return etoTransforms.get(datum);
    }

    public int getDownloadRefreshDays() {
        return downloadRefreshDays;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public int getControlPort() {
        return controlPort;
    }

    public int getTransferPort() {
        return transferPort;
    }

    public int getReprojectionPythonTimeout() {
        return 60 * 10;
    }

    public int getIndexPythonTimeout() {
        return 60 * 5;
    }

    public int getTaskRetries() {
        return 5;
    }

    public void saveProject(ProjectInfo projectInfo) throws Exception {
        Document document = XmlUtils.newDocument("project");
        Element project = document.getDocumentElement();

        // Active
        Element active = document.createElement("active");
        active.appendChild(
                document.createTextNode(Boolean.toString(projectInfo.isActive())));
        project.appendChild(active);

        // Name
        Element projectName = document.createElement("name");
        projectName.appendChild(
                document.createTextNode(projectInfo.getName()));
        project.appendChild(projectName);

        // Start date
        writeStartDate(document, project, projectInfo);

        // Watermask
        Element watermask = document.createElement("watermask");
        watermask.appendChild(
                document.createTextNode(projectInfo.getWatermask()));
        project.appendChild(watermask);

        // Elevation
        Element elevation = document.createElement("elevation");
        elevation.appendChild(
                document.createTextNode(projectInfo.getElevation()));
        project.appendChild(elevation);

        // Minimum LST
        Element minLst = document.createElement("min.lst");
        minLst.appendChild(
                document.createTextNode(Double.toString(projectInfo.getMinLst())));
        project.appendChild(minLst);

        // Maximum LST
        Element maxLst = document.createElement("max.lst");
        maxLst.appendChild(
                document.createTextNode(Double.toString(projectInfo.getMaxLst())));
        project.appendChild(maxLst);

        // Calculate ETa?
        Element calculateETa = document.createElement("calculate.eta");
        calculateETa.appendChild(
                document.createTextNode(
                        Boolean.toString(projectInfo.shouldCalculateETa())));
        project.appendChild(calculateETa);

        // Projection
        writeProjection(document, project, projectInfo.getProjection());

        // Modis tiles
        writeModisTiles(document, project, projectInfo);

        // Zonal summaries
        writeZonalSummaries(document, project, projectInfo);


        // Save to file
        XmlUtils.transformToFile(document, getProjectFilename(projectInfo.getName()));
    }


    private void writeStartDate(Document document, Element project,
            ProjectInfo projectInfo) {

        // Start date
        Element startDate = document.createElement("start.date");
        project.appendChild(startDate);

        // Month
        Element month = document.createElement("month");
        month.appendChild(document.createTextNode(Integer
                .toString(projectInfo.getStartDate().getMonth())));
        startDate.appendChild(month);

        // Day
        Element day = document.createElement("day");
        day.appendChild(document.createTextNode(
                Integer.toString(projectInfo.getStartDate().getDay())));
        startDate.appendChild(day);

        // Year
        Element year = document.createElement("year");
        year.appendChild(document.createTextNode(
                Integer.toString(projectInfo.getStartDate().getYear())));
        startDate.appendChild(year);
    }


    private void writeProjection(Document document, Element project,
            Projection proj) {

        // Projection
        Element projection = document.createElement("projection");
        project.appendChild(projection);

        // Projection type
        Element projectionType = document.createElement("projection.type");
        projectionType.appendChild(document.createTextNode(
                proj.getProjectionType().toString()));
        projection.appendChild(projectionType);

        // Resampling type
        Element resamplingType = document.createElement("resampling.type");
        resamplingType.appendChild(document.createTextNode(
                proj.getResamplingType().toString()));
        projection.appendChild(resamplingType);

        // Datum
        Element datum = document.createElement("datum");
        datum.appendChild(
                document.createTextNode(
                        proj.getDatum().toString()
                )
        );
        projection.appendChild(datum);

        // Pixel size
        Element pixelSize = document.createElement("pixel.size");
        pixelSize.appendChild(
                document.createTextNode(
                        Integer.toString(proj.getPixelSize())
                )
        );
        projection.appendChild(pixelSize);

        // Standard parallel 1
        Element standardParallel1 = document.createElement("standard.parallel1");
        standardParallel1.appendChild(document.createTextNode(
                Double.toString(proj.getStandardParallel1())));
        projection.appendChild(standardParallel1);

        // Standard parallel 2
        Element standardParallel2 = document.createElement("standard.parallel2");
        standardParallel2.appendChild(document.createTextNode(
                Double.toString(proj.getStandardParallel2())));
        projection.appendChild(standardParallel2);

        // Scaling factor
        Element scalingFactor = document.createElement("scaling.factor");
        scalingFactor.appendChild(document.createTextNode(
                Double.toString(proj.getScalingFactor())));
        projection.appendChild(scalingFactor);

        // Central meridian
        Element centralMeridian = document.createElement("central.meridian");
        centralMeridian.appendChild(document.createTextNode(
                Double.toString(proj.getCentralMeridian())));
        projection.appendChild(centralMeridian);

        // False easting
        Element falseEasting = document.createElement("false.easting");
        falseEasting.appendChild(document.createTextNode(
                Double.toString(proj.getFalseEasting())));
        projection.appendChild(falseEasting);

        // False northing
        Element falseNorthing = document.createElement("false.northing");
        falseNorthing.appendChild(document.createTextNode(
                Double.toString(proj.getFalseNorthing())));
        projection.appendChild(falseNorthing);

        // Latitude of origin
        Element latitudeOfOrigin = document.createElement("latitude.of.origin");
        latitudeOfOrigin.appendChild(document.createTextNode(
                Double.toString(proj.getLatitudeOfOrigin())));
        projection.appendChild(latitudeOfOrigin);
    }

    private void writeModisTiles(Document document, Element project, ProjectInfo projectInfo) {
        Element modisTiles = document.createElement("modis.tiles");
        project.appendChild(modisTiles);

        for (ModisTile tile : projectInfo.getModisTiles()) {
            Element modisTile = document.createElement("tile");
            modisTiles.appendChild(modisTile);

            Element horz = document.createElement("horz");
            horz.appendChild(document.createTextNode(Integer.toString(tile.getHTile())));
            modisTile.appendChild(horz);

            Element vert = document.createElement("vert");
            vert.appendChild(document.createTextNode(Integer.toString(tile.getVTile())));
            modisTile.appendChild(vert);
        }
    }

    private void writeZonalSummaries(Document document, Element project, ProjectInfo projectInfo) {
        Element zonalSummaries = document.createElement("zonal.summaries");
        project.appendChild(zonalSummaries);

        for (ZonalSummary summary : projectInfo.getZonalSummaries()) {
            Element zonalSummary = document.createElement("summary");
            zonalSummaries.appendChild(zonalSummary);

            Element name = document.createElement("name");
            name.appendChild(document.createTextNode(summary.getName()));
            zonalSummary.appendChild(name);

            Element shapefile = document.createElement("shapefile");
            shapefile.appendChild(document.createTextNode(summary.getShapeFile()));
            zonalSummary.appendChild(shapefile);

            Element field = document.createElement("field");
            field.appendChild(document.createTextNode(summary.getField()));
            zonalSummary.appendChild(field);
        }
    }

    /**
     * Loads a project file from the projects directory.
     * 
     * @param name Normalized or non-normalized project name to load.
     * @return ProjectInfo The specified project.
     * @throws Exception
     */
    public ProjectInfo loadProject(String name) throws Exception {
        ProjectInfo projectInfo = null;

        boolean active = false;
        String projectName = null;
        DataDate startDate = null;
        String watermask = null;
        String elevation = null;
        double minLst = 0.0;
        boolean gotMinLst = false;
        double maxLst = 0.0;
        boolean gotMaxLst = false;
        Projection projection = null;
        ModisTile[] modisTiles = null;
        ZonalSummary[] zonalSummaries = null;
        boolean calculateETa = false;

        Document document = XmlUtils.parse(getProjectFilename(name));

        Node project = document.getElementsByTagName("project").item(0);

        NodeList projectNodes = project.getChildNodes();
        for (int i=0; i<projectNodes.getLength(); i++) {
            if (projectNodes.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element element = (Element)projectNodes.item(i);
            String nodeName = element.getNodeName();

            if (nodeName.equals("active")) {
                active = Boolean.parseBoolean(element.getTextContent());
            } else if (nodeName.equals("name")) {
                projectName = element.getTextContent();
            } else if (nodeName.equals("start.date")) {
                startDate = readDate(element.getChildNodes());
            } else if (nodeName.equals("watermask")) {
                watermask = element.getTextContent();
            } else if (nodeName.equals("elevation")) {
                elevation = element.getTextContent();
            } else if (nodeName.equals("min.lst")) {
                minLst = Double.parseDouble(element.getTextContent());
                gotMinLst = true;
            } else if (nodeName.equals("max.lst")) {
                maxLst = Double.parseDouble(element.getTextContent());
                gotMaxLst = true;
            } else if (nodeName.equals("projection")) {
                projection = readProjection(element.getChildNodes());
            } else if (nodeName.equals("modis.tiles")) {
                modisTiles = readModisTiles(element.getElementsByTagName("tile"));
            } else if (nodeName.equals("zonal.summaries")) {
                zonalSummaries = readZonalSummaries(element.getElementsByTagName("summary"));
            } else if (nodeName.equals("calculate.eta")) {
                calculateETa = Boolean.parseBoolean(element.getTextContent());
            }
        }

        if (name == null || startDate == null || watermask == null ||
                elevation == null || !gotMinLst || !gotMaxLst ||
                projection == null || modisTiles == null || zonalSummaries == null) {
            throw new Exception("Project configuration file missing required values.");
        }

        projectInfo = new ProjectInfo(
                projectName,
                startDate,
                watermask,
                elevation,
                minLst,
                maxLst,
                projection,
                modisTiles,
                zonalSummaries,
                calculateETa
        );
        projectInfo.setActive(active); // TODO: rearrange

        return projectInfo;
    }

    private DataDate readDate(NodeList nodes) throws Exception {
        int day, month, year;
        day = month = year = -1;

        for (int i=0; i<nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String nodeName = node.getNodeName();
            if (nodeName.equals("day")) {
                day = Integer.parseInt(node.getTextContent());
            } else if (nodeName.equals("month")) {
                month = Integer.parseInt(node.getTextContent());
            } else if (nodeName.equals("year")) {
                year = Integer.parseInt(node.getTextContent());
            }
        }

        if (day == -1 || month == -1 || year == -1) {
            throw new Exception();
        }

        return new DataDate(day, month, year);
    }

    private Projection readProjection(NodeList nodes) throws Exception {
        ProjectionType projectionType = null;
        ResamplingType resamplingType = null;
        double standardParallel1 = 0.0;
        double standardParallel2 = 0.0;
        double scalingFactor = 0.0;
        double centralMeridian = 0.0;
        double falseEasting = 0.0;
        double falseNorthing = 0.0;
        double latitudeOfOrigin = 0.0;
        Projection.Datum datum = null;
        int pixelSize = -1;

        for (int i=0; i<nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String nodeName = node.getNodeName();
            if (nodeName.equals("projection.type")) {
                projectionType = ProjectionType.valueOf(node.getTextContent());
            } else if (nodeName.equals("resampling.type")) {
                resamplingType = ResamplingType.valueOf(node.getTextContent());
            } else if (nodeName.equals("standard.parallel1")) {
                standardParallel1 = Double.parseDouble(node.getTextContent());
            } else if (nodeName.equals("standard.parallel2")) {
                standardParallel2 = Double.parseDouble(node.getTextContent());
            } else if (nodeName.equals("scaling.factor")) {
                scalingFactor = Double.parseDouble(node.getTextContent());
            } else if (nodeName.equals("central.meridian")) {
                centralMeridian = Double.parseDouble(node.getTextContent());
            } else if (nodeName.equals("false.easting")) {
                falseEasting = Double.parseDouble(node.getTextContent());
            } else if (nodeName.equals("false.northing")) {
                falseNorthing = Double.parseDouble(node.getTextContent());
            } else if (nodeName.equals("latitude.of.origin")) {
                latitudeOfOrigin = Double.parseDouble(node.getTextContent());
            } else if (nodeName.equals("datum")) {
                datum = Projection.Datum.valueOf(node.getTextContent());
            } else if (nodeName.equals("pixel.size")) {
                pixelSize = Integer.parseInt(node.getTextContent());
            }
        }

        if (projectionType == null || resamplingType == null ||
                datum == null || pixelSize == -1) {
            throw new Exception();
        }

        return new Projection(
                projectionType, resamplingType, datum, pixelSize,
                standardParallel1, standardParallel2,
                scalingFactor, centralMeridian, falseEasting, falseNorthing,
                latitudeOfOrigin);
    }

    private ModisTile[] readModisTiles(NodeList modisTilesNodes) throws Exception {
        ModisTile[] modisTiles = new ModisTile[modisTilesNodes.getLength()];

        for (int i=0; i<modisTilesNodes.getLength(); i++) {
            final Element tileElement = (Element)modisTilesNodes.item(i);

            final int horz = Integer.parseInt(
                    XmlUtils.getElementByTagName(tileElement, "horz").getTextContent());
            final int vert = Integer.parseInt(
                    XmlUtils.getElementByTagName(tileElement, "vert").getTextContent());

            modisTiles[i] = new ModisTile(horz, vert);
        }

        return modisTiles;
    }

    private ZonalSummary[] readZonalSummaries(NodeList nodes) throws Exception {
        ZonalSummary[] zonalSummaries = new ZonalSummary[nodes.getLength()];

        for (int i=0; i<nodes.getLength(); i++) {
            final Element summaryElement = (Element)nodes.item(i);

            final String name = XmlUtils.getElementByTagName(
                    summaryElement, "name").getTextContent();
            final String shapefile = XmlUtils.getElementByTagName(
                    summaryElement, "shapefile").getTextContent();
            final String field = XmlUtils.getElementByTagName(
                    summaryElement, "field").getTextContent();

            zonalSummaries[i] = new ZonalSummary(name, shapefile, field);
        }

        return zonalSummaries;
    }

    public void setModisDownMode(Mode modisDownMode) {
        this.modisDownMode = modisDownMode;
    }

    public Mode getModisDownMode() {
        return modisDownMode;
    }

    public void setTempDirectory(String tempDirectory) {
        this.tempDirectory = tempDirectory;
    }

    public String getTempDirectory() {
        return tempDirectory;
    }

    public void setEtoDownMode(Mode etoDownMode) {
        this.etoDownMode = etoDownMode;
    }

    public Mode getEtoDownMode() {
        return etoDownMode;
    }

    public void setTrmmDownMode(Mode trmmDownMode) {
        this.trmmDownMode = trmmDownMode;
    }

    public Mode getTrmmDownMode() {
        return trmmDownMode;
    }

    public void setEtoRootDir(String etoRootDir) {
        this.etoRootDir = etoRootDir;
    }

    public String getEtoRootDir() {
        return etoRootDir;
    }

    public void setEtoFtpHostName(String etoFtpHostName) {
        this.etoFtpHostName = etoFtpHostName;
    }

    public String getEtoFtpHostName() {
        return etoFtpHostName;
    }

    public void setTrmmUserName(String trmmUserName) {
        this.trmmUserName = trmmUserName;
    }

    public String getTrmmUserName() {
        return trmmUserName;
    }

    public void setTrmmPassWord(String trmmPassWord) {
        this.trmmPassWord = trmmPassWord;
    }

    public String getTrmmPassWord() {
        return trmmPassWord;
    }

    public void setEtoUserName(String etoUserName) {
        this.etoUserName = etoUserName;
    }

    public String getEtoUserName() {
        return etoUserName;
    }

    public void setEtoPassWord(String etoPassWord) {
        this.etoPassWord = etoPassWord;
    }

    public String getEtoPassWord() {
        return etoPassWord;
    }

    public void setModisUserName(String modisUserName) {
        this.modisUserName = modisUserName;
    }

    public String getModisUserName() {
        return modisUserName;
    }

    public void setModisPassWord(String modisPassWord) {
        this.modisPassWord = modisPassWord;
    }

    public String getModisPassWord() {
        return modisPassWord;
    }



    public String getModisLstUrl() {
        return modisLstUrl;
    }

    public void setModisLstUrl(String modislstUrl) {
        modisLstUrl = modislstUrl;
    }

    public String getModisNbarUrl() {
        return modisNbarUrl;
    }

    public void setModisNbarUrl(String modisnabrUrl) {
        modisNbarUrl = modisnabrUrl;
    }

    public void setTRMM_3B42FtpHostName(String tRMM3B42FtpHostName) {
        TRMM_3B42FtpHostName = tRMM3B42FtpHostName;
    }

    public String getTRMM_3B42FtpHostName() {
        return TRMM_3B42FtpHostName;
    }

    public void setTRMM_3B42RootDir(String tRMM3B42RootDir) {
        TRMM_3B42RootDir = tRMM3B42RootDir;
    }

    public String getTRMM_3B42RootDir() {
        return TRMM_3B42RootDir;
    }

    public void setTRMM_3B42UserName(String tRMM3B42UserName) {
        TRMM_3B42UserName = tRMM3B42UserName;
    }

    public String getTRMM_3B42UserName() {
        return TRMM_3B42UserName;
    }

    public void setTRMM_3B42PassWord(String tRMM3B42PassWord) {
        TRMM_3B42PassWord = tRMM3B42PassWord;
    }

    public String getTRMM_3B42PassWord() {
        return TRMM_3B42PassWord;
    }

    public void setTRMM_3B42Url(String tRMM3B42Url) {
        TRMM_3B42Url = tRMM3B42Url;
    }

    public String getTRMM_3B42Url() {
        return TRMM_3B42Url;
    }

    public void setTRMM_3B42DownMode(Mode tRMM_3B42DownMode) {
        TRMM_3B42DownMode = tRMM_3B42DownMode;
    }

    public Mode getTRMM_3B42DownMode() {
        return TRMM_3B42DownMode;
    }

    public void setProductList(String[] productList) {
        this.productList = productList;
    }

    public String[] getProductList() {
        return productList;
    }

    public void setNLDASFtpHostName(String nLDASFtpHostName) {
        NLDASFtpHostName = nLDASFtpHostName;
    }

    public String getNLDASFtpHostName() {
        return NLDASFtpHostName;
    }

    public void setNLDASRootDir(String nLDASRootDir) {
        NLDASRootDir = nLDASRootDir;
    }

    public String getNLDASRootDir() {
        return NLDASRootDir;
    }

    public void setNLDASUserName(String nLDASUserName) {
        NLDASUserName = nLDASUserName;
    }

    public String getNLDASUserName() {
        return NLDASUserName;
    }

    public void setNLDASPassWord(String nLDASPassWord) {
        NLDASPassWord = nLDASPassWord;
    }

    public String getNLDASPassWord() {
        return NLDASPassWord;
    }

    public void setNLDASUrl(String nLDASUrl) {
        NLDASUrl = nLDASUrl;
    }

    public String getNLDASUrl() {
        return NLDASUrl;
    }

    public void setNLDASDownMode(Mode nLDASDownMode) {
        NLDASDownMode = nLDASDownMode;
    }

    public Mode getNLDASDownMode() {
        return NLDASDownMode;
    }

}

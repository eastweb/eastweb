package edu.sdstate.eastweb.prototype.scheduler.tasks;

import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;
import edu.sdstate.eastweb.prototype.*;
import edu.sdstate.eastweb.prototype.download.EtoDownloadMetadata;
import edu.sdstate.eastweb.prototype.reprojection.*;
import edu.sdstate.eastweb.prototype.scheduler.framework.RunnableTask;

/**
 * This class is a Task that composites and reprojects ETo data.
 * The composite period is the day specified and the seven days following it.
 * 
 * @author Michael VanBemmel
 */
public class ReprojectEtoTask implements RunnableTask {
    private final ProjectInfo mProject;
    private final DataDate mDate;
    private static File tempDictionary;
    public ReprojectEtoTask(ProjectInfo project, DataDate date) throws IOException {
        tempDictionary=new File(Config.getInstance().getTempDirectory());
        mProject = project;
        mDate = date;
    }

    private File getMetadataFile() throws ConfigReadException {
        return DirectoryLayout.getEtoReprojectedMetadata(mProject, mDate);
    }

    private File getOutputFile() throws ConfigReadException {
        return DirectoryLayout.getEtoReprojected(mProject, mDate);
    }

    private EtoReprojectedMetadata makeMetadata() throws IOException {
        final EtoDownloadMetadata download = EtoDownloadMetadata.fromFile(
                DirectoryLayout.getEtoDownloadMetadata(mDate));

        final long timestamp = new Date().getTime();

        return new EtoReprojectedMetadata(download, timestamp);
    }

    @Override
    public boolean getCanSkip() {
        try {
            EtoReprojectedMetadata.fromFile(getMetadataFile());

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private File makeWorkspace() throws IOException {
        final File workspace = File.createTempFile("wksp", null,tempDictionary);
        FileUtils.forceDelete(workspace);
        FileUtils.forceMkdir(workspace);
        return workspace;
    }

    @Override
    public void run() throws Exception {
        // Remove the directory if it already exists so the script won't run into problems
        FileUtils.deleteQuietly(getOutputFile().getParentFile());

        final File workspace = makeWorkspace();
        try {
            // The composite period is the day specified and the seven days following it
            final List<File> inFiles = new ArrayList<File>();
            //final File[] inFiles = new File[8];
            for (int i = 0; i < 8; ++i) {
                inFiles.add(DirectoryLayout.getEtoDownloadMainFile(mDate.next(i)));
            }

            final File outFile = getOutputFile();
            FileUtils.forceMkdir(outFile.getParentFile());
            //new EtoReprojection().reprojectEto(workspace, inFiles, mProject.getProjection(), outFile);

            final File compositeFile = new File(outFile.getParentFile(), "composite.tif");

            new GdalCompositeEto().composite(inFiles, compositeFile);
            new GdalProjectEto().project(compositeFile, mProject, outFile);

            compositeFile.delete();
        } finally {
            FileUtils.deleteQuietly(workspace);
        }

        // Write a metadata file
        final File metadataFile = getMetadataFile();
        FileUtils.forceMkdir(metadataFile.getParentFile());
        makeMetadata().toFile(metadataFile);
    }

    @Override
    public String getName() {
        return String.format(
                "Composite and reproject ETo: project=\"%s\", date=%s",
                mProject.getName(),
                mDate.toCompactString()
        );
    }

}
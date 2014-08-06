package edu.sdstate.eastweb.prototype.reprojection.tests;

import java.io.File;

import edu.sdstate.eastweb.prototype.Config;
import edu.sdstate.eastweb.prototype.ConfigReadException;
import edu.sdstate.eastweb.prototype.DataDate;
import edu.sdstate.eastweb.prototype.ProjectInfo;
import edu.sdstate.eastweb.prototype.download.ModisProduct;
import edu.sdstate.eastweb.prototype.reprojection.ModisReprojection;
import edu.sdstate.eastweb.prototype.scheduler.tasks.PrepareModisTask;

public class ModisReprojectionTest {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // testETo();
        // testTRMM();
        testModis();
        // System.out.println("end test");
        // testByProcess();
    }

    public static void testModis() throws Exception {
        ProjectInfo projection = Config.getInstance().loadProject("tw_test");
        // Projection project=projection.getProjection();

        // File input1 = new
        // File("C:\\Users\\jiameng\\Desktop\\mozaic\\h10v05\\tile.hdf");
        // File input2 = new
        // File("C:\\Users\\jiameng\\Desktop\\mozaic\\h11v05\\tile.hdf");
        // File output = new
        // File("C:\\Users\\general\\Desktop\\outputOldband2.tif");

        /*
         * new ModisReprojection().reprojectModis( new File[] { input1, input2
         * }, new boolean []{false,true}, project, output);
         */

        // TiffFixer.fixTiff(new
        // File("C:\\Users\\jiameng\\Desktop\\outputOldband2.QC_Day.tif"), new
        // File("C:\\Users\\jiameng\\Desktop\\outputOldband2.QC_Day_fixed.tif"));

        new ModisReprojection()
        .project(
                new File[] {
                        new File(
                                "E:\\eastweb-data\\download\\modis-nbar\\2013\\209\\h28v06\\tile.hdf"),
                                new File(
                                        "E:\\eastweb-data\\download\\modis-nbar\\2013\\209\\h29v06\\tile.hdf") },
                                        projection,
                                        new File[] {
                        new File(
                                "C:\\Users\\general\\Desktop\\output\\209_n1.tif"),
                                new File(
                                        "C:\\Users\\general\\Desktop\\output\\209_n2.tif"),
                                        // new
                                        // File("C:\\Users\\general\\Desktop\\output\\209_n3.tif"),
                                        // new
                                        // File("C:\\Users\\general\\Desktop\\output\\209_n4.tif"),
                                        // new
                                        // File("C:\\Users\\general\\Desktop\\output\\209_n5.tif"),
                                        // new
                                        // File("C:\\Users\\general\\Desktop\\output\\209_n6.tif"),
                                        // new
                                        // File("C:\\Users\\general\\Desktop\\output\\209_n7.tif"),
                }, new int[] { 1, 2 });

        // new ModisReprojection().project(
        // new File[] {
        // new
        // File("E:\\eastweb-data\\download\\modis-nbar\\2013\\233\\h28v06\\tile.hdf"),
        // new
        // File("E:\\eastweb-data\\download\\modis-nbar\\2013\\233\\h29v06\\tile.hdf")
        // },
        // projection,
        // new File[] {
        // new File("C:\\Users\\general\\Desktop\\output\\233_l.tif"),
        // // new File("C:\\Users\\jiameng\\Desktop\\output\\band2r.tif"),
        // // new File("C:\\Users\\jiameng\\Desktop\\output\\band3r.tif")
        // }
        // ,
        // new int[] {1});
        // new ModisReprojection().project(
        // new File[] {
        // new
        // File("E:\\eastweb-data\\download\\modis-nbar\\2013\\233\\h28v06\\tile.hdf"),
        // new
        // File("E:\\eastweb-data\\download\\modis-nbar\\2013\\233\\h29v06\\tile.hdf")
        // },
        // projection,
        // new File[] {
        // new File("C:\\Users\\general\\Desktop\\output\\233_n1.tif"),
        // new File("C:\\Users\\general\\Desktop\\output\\233_n2.tif"),
        // new File("C:\\Users\\general\\Desktop\\output\\233_n3.tif"),
        // new File("C:\\Users\\general\\Desktop\\output\\233_n4.tif"),
        // new File("C:\\Users\\general\\Desktop\\output\\233_n5.tif"),
        // new File("C:\\Users\\general\\Desktop\\output\\233_n6.tif"),
        // new File("C:\\Users\\general\\Desktop\\output\\233_n7.tif"),
        // }
        // ,
        // new int[] {1,2,3,4,5,6,7});

        // new ModisReprojection().project(
        // new File[] {
        // new
        // File("E:\\eastweb-data\\download\\modis-nbar\\2013\\225\\h28v06\\tile.hdf"),
        // new
        // File("E:\\eastweb-data\\download\\modis-nbar\\2013\\225\\h29v06\\tile.hdf")
        // },
        // projection,
        // new File[] {
        // new File("C:\\Users\\general\\Desktop\\output\\225_l.tif"),
        // // new File("C:\\Users\\jiameng\\Desktop\\output\\band2r.tif"),
        // // new File("C:\\Users\\jiameng\\Desktop\\output\\band3r.tif")
        // }
        // ,
        // new int[] {1});
        // new ModisReprojection().project(
        // new File[] {
        // new
        // File("E:\\eastweb-data\\download\\modis-nbar\\2013\\225\\h28v06\\tile.hdf"),
        // new
        // File("E:\\eastweb-data\\download\\modis-nbar\\2013\\225\\h29v06\\tile.hdf")
        // },
        // projection,
        // new File[] {
        // new File("C:\\Users\\general\\Desktop\\output\\225_n1.tif"),
        // new File("C:\\Users\\general\\Desktop\\output\\225_n2.tif"),
        // new File("C:\\Users\\general\\Desktop\\output\\225_n3.tif"),
        // new File("C:\\Users\\general\\Desktop\\output\\225_n4.tif"),
        // new File("C:\\Users\\general\\Desktop\\output\\225_n5.tif"),
        // new File("C:\\Users\\general\\Desktop\\output\\225_n6.tif"),
        // new File("C:\\Users\\general\\Desktop\\output\\225_n7.tif"),
        // }
        // ,
        // new int[] {1,2,3,4,5,6,7});

        // File[] input, boolean bands[],ProjectInfo project, File outputFiles
        /*
         * new ModisReprojectionReady().project(new File[] { new
         * File("C:\\Users\\general\\Desktop\\mozaic\\h10v05\\tile.hdf"), new
         * File("C:\\Users\\general\\Desktop\\mozaic\\h11v05\\tile.hdf") }, new
         * boolean[]{true,false,false,true}, projection, new
         * File("c:\\Users\\general\\Desktop\\output") );
         */
    }

    public static void testByProcess() throws ConfigReadException, Exception {
        ModisProduct lst = ModisProduct.LST;
        ModisProduct nbar = ModisProduct.NBAR;
        DataDate d1 = new DataDate(29, 8, 2013);
        //DataDate d2 = new DataDate(6, 9, 2013);
        //DataDate d3 = new DataDate(22, 9, 2013);

        ProjectInfo projection1 = Config.getInstance().loadProject("tw_test");
        ProjectInfo projection2 = Config.getInstance().loadProject("tw_test");
        //ProjectInfo projection3 = Config.getInstance().loadProject("tw_test");
        //ProjectInfo projection4 = Config.getInstance().loadProject("tw_test");
        //ProjectInfo projection5 = Config.getInstance().loadProject("tw_test");
        //ProjectInfo projection6 = Config.getInstance().loadProject("tw_test");

        PrepareModisTask p1 = new PrepareModisTask(projection1, lst, d1);
        PrepareModisTask p2 = new PrepareModisTask(projection2, nbar, d1);
        // PrepareModisTask p3=new PrepareModisTask(projection3, lst, d2);
        // PrepareModisTask p4=new PrepareModisTask(projection4, nbar, d2);
        // PrepareModisTask p5=new PrepareModisTask(projection5, lst, d3);
        // PrepareModisTask p6=new PrepareModisTask(projection6, nbar, d3);

        p1.run();
        p2.run();
        // p3.run();
        // p4.run();
        // p5.run();
        // p6.run();

    }

    public static void testETo() throws Exception {
    }

    /*
     * public static void testTRMM() throws Exception { Projection ngp = new
     * Projection( ProjectionType.ALBERS_EQUAL_AREA, ResamplingType.BILINEAR,
     * Datum.NAD83, 0, //pixel size 0, 0, // semi axis 43, 47, // std par 0, //
     * scaling fact -102, // cent mer 0, 0, // fe and fn 45 // lat of orig ); //
     * Projection ethiopia = new Projection( //
     * ProjectionType.TRANSVERSE_MERCATOR, // ResamplingType.BILINEAR, //
     * Datum.WGS84, // 1000, //pixel size // 0, 0, // semi axis // 0, 0, // std
     * par // 0.9996, // scaling fact // 39, // cent mer // 500000.0,
     * 10000000.0, // fe and fn // 45 // lat of orig // ); TrmmReprojection r =
     * new TrmmReprojection(); r.reprojectTrmm( new File("."), new
     * File("Z:\\IndexComputations\\TRMM\\3b42_daily.2011.04.27.6.bin.nc"), ngp,
     * new File("C:\\Users\\isaiah\\Desktop\\TRMM\\trmm") ); }
     */

}

package edu.sdstate.eastweb.prototype.reprojection.tests;

public class gdalOpen {

    public static void main(String[] args) throws Exception {
        /*
        int round = 1;
        Runan fc = new Runan();
        fc.myRun(round);
         */
        /*
        int i = 1;
        GdalUtils.register();
        synchronized (GdalUtils.lockObject) {


            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band6") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band5") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band4") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band3") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band2") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band1") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band7") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band1") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band2") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band3") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band4") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band5") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band6") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band7") == null ) { System.out.println( i +"is wrong" ); }i++;


            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\241\\h29v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Day_1km") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open( "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\241\\h29v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Night_1km") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\241\\h28v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Day_1km") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open( "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\241\\h28v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Night_1km") == null ) { System.out.println( i +"is wrong" ); }i++;


            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\273\\h29v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Day_1km") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open( "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\273\\h29v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Night_1km") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\273\\h28v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Day_1km") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open( "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\273\\h28v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Night_1km") == null ) { System.out.println( i +"is wrong" ); }i++;

            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band6") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band5") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band4") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band3") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band2") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band1") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band7") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band1") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band2") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band3") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band4") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band5") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band6") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band7") == null ) { System.out.println( i +"is wrong" ); }i++;

        }
         */
        openRun p1=new openRun(new String[]{
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band6",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band5",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band4",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band3",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band2",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band1",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band7",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band1",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band2",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band3",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band4",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band5",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band6",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band7"
        });

        openRun p2=new openRun(new String[]{
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band6",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band5",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band4",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band3",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band2",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band1",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band7",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band1",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band2",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band3",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band4",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band5",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band6",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band7"
        });
        openRun p3=new openRun(new String[]{
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\273\\h29v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Day_1km",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\273\\h29v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Night_1km",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\273\\h28v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Day_1km",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\273\\h28v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Night_1km"
        });

        openRun p4=new openRun(new String[]{
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\241\\h29v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Day_1km",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\241\\h29v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Night_1km",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\241\\h28v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Day_1km",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\241\\h28v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Night_1km"
        });
        /*
        openRun p5=new openRun(new String[]{
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band6",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band5",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band4",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band3",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band2",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band1",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band7",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band1",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band2",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band3",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band4",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band5",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band6",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\241\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band7"
        });

        openRun p6=new openRun(new String[]{
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band6",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band5",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band4",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band3",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band2",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band1",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h29v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band7",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band1",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band2",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band3",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band4",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band5",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band6",
                "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-nbar\\2013\\273\\h28v06\\tile.hdf\":MOD_Grid_BRDF:Nadir_Reflectance_Band7"
        });
         */

        p4.run();

        p3.run();


        p1.run();
        p2.run();

        // p5.run();
        // p6.run();
        System.out.println("over!");



    }
    /*
    void myRun( int _roundCount )
    {

    }*/
}


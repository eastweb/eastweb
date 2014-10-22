package edu.sdstate.eastweb.prototype.reprojection.tests;

import org.gdal.gdal.gdal;

import edu.sdstate.eastweb.prototype.util.GdalUtils;

public class Runan {

    public void myRun( int round )
    {
        switch(round)
        {
        case 1:
            case1();
            break;
        case 2:
            case2();
            break;
        case 3:
            case3();
            break;
        case 4:
            case4();
            break;
        default:
            System.out.println("ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
            break;
        }
        return;
    }

    private void case1()
    {
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
        }

        Runan myNewObject = new Runan();
        myNewObject.myRun(2);
    }

    private void case2()
    {
        int i = 15;
        GdalUtils.register();
        synchronized (GdalUtils.lockObject) {
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

        Runan myNewObject = new Runan();
        myNewObject.myRun(3);
    }

    private void case3()
    {
        int i = 30;
        GdalUtils.register();
        synchronized (GdalUtils.lockObject) {
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\241\\h29v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Day_1km") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open( "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\241\\h29v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Night_1km") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\241\\h28v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Day_1km") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open( "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\241\\h28v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Night_1km") == null ) { System.out.println( i +"is wrong" ); }i++;
        }

        Runan myNewObject = new Runan();
        myNewObject.myRun(4);
    }

    private void case4()
    {
        int i = 34;
        GdalUtils.register();
        synchronized (GdalUtils.lockObject) {

            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\273\\h29v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Day_1km") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open( "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\273\\h29v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Night_1km") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open("HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\273\\h28v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Day_1km") == null ) { System.out.println( i +"is wrong" ); }i++;
            if( gdal.Open( "HDF4_EOS:EOS_GRID:\"E:\\eastweb-data\\download\\modis-lst\\2013\\273\\h28v06\\tile.hdf\":MODIS_Grid_8Day_1km_LST:LST_Night_1km") == null ) { System.out.println( i +"is wrong" ); }i++;
        }

    }

}

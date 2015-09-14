package edu.sdstate.eastweb.prototype.reprojection.tests;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;

import edu.sdstate.eastweb.prototype.scheduler.framework.RunnableTask;
import edu.sdstate.eastweb.prototype.util.GdalUtils;


public class openRun implements RunnableTask{
    String[] name;
    openRun(String[] a){

        name=a;
    }
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void run() throws Exception {
        GdalUtils.register();
        synchronized (GdalUtils.lockObject) {
            // TODO Auto-generated method stub
            int i = 0;
            while(true){
                if( i >= name.length )
                {
                    break;
                }

                //for(int i=0;i<name.length;i++){
                Dataset temp=gdal.Open(name[i]);
                if(temp==null){
                    System.out.println("open failed: " + name[i] + ". Sleep 1 second. ");
                    Thread.sleep(1000);
                    continue;
                }
                else
                {
                    i++;
                    continue;
                }
            }
        }
    }

    @Override
    public boolean getCanSkip() {
        // TODO Auto-generated method stub
        return false;
    }

}

package jingsong.oracle.smart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class DataFilter {
    private   final Logger log = LoggerFactory.getLogger(DataFilter.class);
    @Resource
    ScaleRule scaleRule;
     public void setNodeMetric(CpuMetricModel cpuMetricModel  ) {
         printOneNodeInfo(cpuMetricModel);
         CpuMetricModel finalCpuMetricModel = compare2Nodes(cpuMetricModel);
         scaleRule.compute(finalCpuMetricModel);

    }

    private void printOneNodeInfo(CpuMetricModel cpuMetricModel) {
        log.info(cpuMetricModel.getClusterId());
        log.info("<============= node {} =============>", cpuMetricModel.getVmId());
        log.info("  ld_1 {}", cpuMetricModel.getLd_1());
        log.info("  ld_5 {}", cpuMetricModel.getLd_5());
        log.info("  ld_15 {}", cpuMetricModel.getLd_15());
        log.info("  cpu usage {} %", cpuMetricModel.getCpuUsage());
        log.info("getMincore {}  ", cpuMetricModel.getMincore());
        log.info("getMaxcore {}  ", cpuMetricModel.getMaxcore());
        log.info("getQweight {}  ", cpuMetricModel.getQweight());
    }



    /**
     *
     * @param cpuMetricModel  cpuMetricModel is always newer
     * @return
     */
     CpuMetricModel compare2Nodes(CpuMetricModel cpuMetricModel){
         String clusterId = cpuMetricModel.getClusterId();
         String vmId=cpuMetricModel.getVmId();
         String otherVm = otherVmId(vmId);

         CpuMetricModel cpuMetricModel2=Constant.metricModelHashMap.get(clusterId+"="+otherVm);

         if(cpuMetricModel2!=null){
             Double cpuUsage1 = Double.parseDouble(cpuMetricModel.getCpuUsage());
             Double cpuUsage2 = Double.parseDouble(cpuMetricModel2.getCpuUsage());

             if(cpuUsage1>=cpuUsage2 )
             {
                 return cpuMetricModel;
             }else {
                 return  cpuMetricModel2;
             }

         }else {
             Timer timer = new Timer();
             TimeOutTask timeOutTask=new TimeOutTask(timer);
             timer.schedule(  timeOutTask,50*1000,1000);
             while (timeOutTask.open==false )
             {
             }
             return cpuMetricModel;
//             HashMap<String,CpuMetricModel> vmMap = new HashMap();
//             vmMap.put(cpuMetricModel.getVmId(), cpuMetricModel);
//             holderMap.put(cpuMetricModel.getClusterId(), vmMap);
         }
     }
     String otherVmId(String vmId){
         if("1".equals(vmId)){
             return "2";
         } else return "1";
     }

    public static void main(String[] args) {
//        new DataFilter().getCpuUsage();

        HashMap<String, String> map = new HashMap<>();
        map.put("1","33");
        map.remove("1");
        for (Map.Entry<String,String> entry: map.entrySet()
             ) {
            System.out.println(entry.getKey());
        }

    }



}
class  TimeOutTask   extends TimerTask{
    private Timer timer;
    public boolean open=false;
    public TimeOutTask(  Timer timer) {

        this.timer = timer;
    }
    @Override
    public void run() {
        open=true;
        timer.cancel();

    }
}
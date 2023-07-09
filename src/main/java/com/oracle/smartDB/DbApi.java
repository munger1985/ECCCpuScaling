package com.oracle.smartDB;

import com.oracle.bmc.database.DatabaseClient;
import com.oracle.bmc.database.model.UpdateVmClusterDetails;
import com.oracle.bmc.database.requests.GetVmClusterRequest;
import com.oracle.bmc.database.requests.UpdateVmClusterRequest;
import com.oracle.bmc.database.responses.GetVmClusterResponse;
import com.oracle.bmc.database.responses.UpdateVmClusterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbApi {
    private static final Logger log = LoggerFactory.getLogger(DbApi.class);
    private final DatabaseClient databaseClient;


    String[] param =new String[3];

    public DbApi(DatabaseClient  databaseClient) {
        this.databaseClient = databaseClient;
    }

    String run() {


        /* Create a service client */
        Integer core = Integer.valueOf(param[1]);
        /* Create a request and dependent object(s). */
        UpdateVmClusterDetails updateVmClusterDetails = UpdateVmClusterDetails.builder()
                .cpuCoreCount(core)
                .build();

        String clusterId=param[0];

        UpdateVmClusterRequest updateVmClusterRequest = UpdateVmClusterRequest.builder()
                .vmClusterId(clusterId)
                .updateVmClusterDetails(updateVmClusterDetails)
                .build();

        /* Send request to the Client */
        try {
            UpdateVmClusterResponse response = databaseClient.updateVmCluster(updateVmClusterRequest);
            String requestId = response.getOpcRequestId();
            log.info("requestId {}", requestId);

            String workrequestId = response.getOpcWorkRequestId();
            log.info("work request Id {}", workrequestId);

            Integer cpuCount = response.getVmCluster().getCpusEnabled();
            String template = " https://cloud.oracle.com/exacc/{}/workrequests/{}/logs";
            log.info("work request url used in browser");
            log.info(template,response.getVmCluster().getId(), workrequestId);
            log.info("current cluster {} core count is {}", response.getVmCluster().getId(), cpuCount);
            return "Success";
        }
        catch (Exception e){
            System.out.println(e.fillInStackTrace());
            return "Fail";
        }

    }


    Integer getCurrentCpuCount(String vmclusterId){
        GetVmClusterRequest getVmClusterRequest= GetVmClusterRequest.builder().vmClusterId(vmclusterId).build();
       GetVmClusterResponse getVmClusterResponse= databaseClient.getVmCluster(getVmClusterRequest);
        Integer cpuCount = getVmClusterResponse.getVmCluster().getCpusEnabled();

        return cpuCount;
    }

}

package com.jscape.inet.mft.functions.repo;

import com.jscape.inet.mft.Function;
import com.jscape.inet.mft.management.client.api.ManagerSubsystem;
import com.jscape.inet.mftserver.operation.key.CertificateSummary;
import com.jscape.inet.mftserver.operation.key.ServerKeySummary;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by hari on 8/4/2018.
 */
public class IsCertificateValid extends Function {

    private String USER_DIR = "user.dir";
    private String FOLDER_NAME = "etc";
    private String CLIENT_CONFIG_FILE_NAME = "client.cfg";

    @Override
    public String getName() {
        return "IsCertificateValid";
    }

    @Override
    public Map<String, String> getTemplates() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("IsCertificateValid(days)", "Returns true if all certificate are valid before the number of days than the expired date, false otherwise ");
        return map;
    }

    @Override
    public Object evaluate(Object[] value) {
        boolean isValid = true;
        int days = Integer.parseInt(value[0].toString());
        String instPath = System.getProperty(USER_DIR) + File.separator + FOLDER_NAME + File.separator + CLIENT_CONFIG_FILE_NAME;
        ManagerSubsystem managerSubsystem = null;
        try {
            managerSubsystem = new ManagerSubsystem(instPath);
            managerSubsystem.connect();
            CertificateSummary[] certificates = managerSubsystem.clientCertificateSummaries();
            for (CertificateSummary certificate : certificates) {
                long diffInMilliSecs = Math.abs(Calendar.getInstance().getTimeInMillis() - certificate.getCertificateEndDate());
                long diff = TimeUnit.DAYS.convert(diffInMilliSecs, TimeUnit.MILLISECONDS);
                if (diff <= days) {
                    isValid = false;
                    break;
                }
            }
        } catch (Exception e) {
            if (managerSubsystem != null) managerSubsystem.close();
            System.out.println("Not able to connect manager system : " + e); // Check for exception in System out log
        } finally {
            if (managerSubsystem != null) managerSubsystem.close();
        }
        return isValid;
    }
}

public class SmsService {

    private SmsDataAccess smsDataAccess = new SmsDataAccess();
    private Utility utility = new Utility();

    public void fetchSmsAccount() {
        String AK = smsDataAccess.getAppKey();
        String AS = smsDataAccess.getAppSecret();
        String CK = smsDataAccess.getConsumerKey();
        String METHOD = "GET";
        String QUERY_URL = "https://eu.api.ovh.com/1.0/sms/";
        String BODY = "";

        try {
            long TSTAMP = utility.getTimestamp();
            String toSign = AS + "+" + CK + "+" + METHOD + "+" + QUERY_URL + "+" + BODY + "+" + TSTAMP;
            String signature = "$1$" + utility.hashSHA1(toSign);

            HttpURLConnection req = utility.prepareHttpRequest(QUERY_URL, METHOD, AK, CK, signature, TSTAMP, BODY);
            utility.processHttpResponse(req);

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public void sendSmsMessage() {
        String AK = smsDataAccess.getAppKey();
        String AS = smsDataAccess.getAppSecret();
        String CK = smsDataAccess.getConsumerKey();
        String ServiceName = smsDataAccess.getServiceName();
        String METHOD = "POST";
        String QUERY_URL = "https://eu.api.ovh.com/1.0/sms/" + ServiceName + "/jobs";
        String BODY = "{\"receivers\":[\"+33612345678\"],\"message\":\"Test SMS OVH\",\"priority\":\"high\",\"senderForResponse\":true}";

        try {
            long TSTAMP = utility.getTimestamp();
            String toSign = AS + "+" + CK + "+" + METHOD + "+" + QUERY_URL + "+" + BODY + "+" + TSTAMP;
            String signature = "$1$" + utility.hashSHA1(toSign);

            HttpURLConnection req = utility.prepareHttpRequest(QUERY_URL, METHOD, AK, CK, signature, TSTAMP, BODY);
            utility.processHttpResponse(req);

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}

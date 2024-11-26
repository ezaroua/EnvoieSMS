public class SmsController {

    private SmsService smsService = new SmsService();

    public void getSmsAccount() {
        smsService.fetchSmsAccount();
    }

    public void sendSms() {
        smsService.sendSmsMessage();
    }
}

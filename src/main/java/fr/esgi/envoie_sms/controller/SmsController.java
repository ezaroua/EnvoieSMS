package fr.esgi.envoie_sms.controller;

import fr.esgi.envoie_sms.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SmsController {

    @Autowired
    private SmsService SmsService;

    @GetMapping("/")
    public String showForm() {
        return "smsForm";
    }

    @PostMapping("/send")
    public String sendSms(@RequestParam String phoneNumber,
                          @RequestParam String message,
                          Model model) {
        try {
            String result = SmsService.sendSms(phoneNumber, message);
            model.addAttribute("success", "SMS envoyé avec succès!");
            model.addAttribute("response", result);
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'envoi: " + e.getMessage());
        }
        return "result";
    }

    @GetMapping("/history")
    public String showHistory(Model model) {
        model.addAttribute("messages", SmsService.getHistory());
        return "history";
    }
}
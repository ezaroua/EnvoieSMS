package fr.esgi.envoie_sms.repository;

import fr.esgi.envoie_sms.model.Sms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SmsRepository extends JpaRepository<Sms, Long> {
    List<Sms> findAllByOrderBySentAtDesc();
}
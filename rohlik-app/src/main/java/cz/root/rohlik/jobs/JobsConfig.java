package cz.root.rohlik.jobs;

import cz.root.rohlik.service.RohlikService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class JobsConfig {

    @Autowired
    private RohlikService rohlikService;

    // kazdych 30 minut se podivame, jestli neni nejaka objednavka uz prosla ve stavu register
    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void checkAllUnPaidOrders() {
        rohlikService.checkAllUnPaidOrders();
    }
}

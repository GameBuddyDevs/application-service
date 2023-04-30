package com.back2261.applicationservice.domain.service;

import com.back2261.applicationservice.interfaces.request.SendNotificationTokenRequest;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "notification-service", url = "${notification-service.url}")
public interface NotificationService {

    @PostMapping(value = "/notif/token")
    DefaultMessageResponse sendToToken(SendNotificationTokenRequest tokenRequest);
}

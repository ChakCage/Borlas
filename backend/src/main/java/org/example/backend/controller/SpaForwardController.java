package org.example.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Отдаёт index.html для клиентских (SPA) маршрутов при прямом заходе/F5,
 * чтобы React-роутер сам дальше разобрал путь.
 */
@Controller
public class SpaForwardController {

    // Явно перечисляем роуты твоего фронта
    @GetMapping({
            "/",                 // корень
            "/login",
            "/posts",
            "/create",
            "/my-posts",
            "/deleted",
            "/profile"
    })
    public String forwardKnownSpaRoutes() {
        // В resources/static лежит собранный фронт,
        // поэтому форвардим на index.html
        return "forward:/index.html";
    }
}

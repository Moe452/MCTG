package com.moe.controller;

import com.moe.http.base.HttpRequest;
import com.moe.persistence.CardRepository;
import com.moe.model.Package;
import com.moe.persistence.PackageRepository;
import com.moe.http.base.HttpResponse;
import com.moe.http.base.HttpServlet;
import com.moe.model.User;
import com.google.gson.Gson;

public class TransactionController extends HttpServlet {
    PackageRepository packageRepository;
    CardRepository cardRepository;
    Gson gson;

    public TransactionController() {
        gson = new Gson();
        this.packageRepository = PackageRepository.getInstance();
        this.cardRepository = CardRepository.getInstance();
    }

    public HttpResponse handleAcquirePackage(HttpRequest request) {
        // Only authorized users can acquire packages
        if (request.getAuthUser() == null) return HttpResponse.unauthorized();

        User user = request.getAuthUser();
        Package cardPackage = packageRepository.getRandomPackage();

        if (cardPackage != null && packageRepository.addPackageToUser(cardPackage, user)) {
            return HttpResponse.ok();
        }

        return HttpResponse.internalServerError();
    }
}

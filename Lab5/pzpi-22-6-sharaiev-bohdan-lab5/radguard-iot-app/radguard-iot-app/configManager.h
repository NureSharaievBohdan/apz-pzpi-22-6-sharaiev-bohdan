#pragma once

#include <string>
#include <nlohmann/json.hpp>

namespace Config {
    nlohmann::json loadConfig(const std::string& filePath);

    const char* getBaseUrl(const nlohmann::json& config);
    const char* getSensorApi(const nlohmann::json& config);
    const char* getLocationApi(const nlohmann::json& config);
    const char* getRadiationApi(const nlohmann::json& config);
    const char* getLoginApi(const nlohmann::json& config);

    const char* getEmail(const nlohmann::json& config);
    const char* getPassword(const nlohmann::json& config);

    double getLowRadiationLevel(const nlohmann::json& config);
    double getModerateRadiationLevel(const nlohmann::json& config);
    double getHighRadiationLevel(const nlohmann::json& config);
    double getCriticalRadiationLevel(const nlohmann::json& config);

    int getSensorUpdateIntervalMs(const nlohmann::json& config);
}

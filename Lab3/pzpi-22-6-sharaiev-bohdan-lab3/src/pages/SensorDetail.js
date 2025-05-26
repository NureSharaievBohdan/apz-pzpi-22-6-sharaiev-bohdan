import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import "../styles/SensorDetail.css";
import { useTranslation } from "react-i18next";

export default function SensorDetails() {
  const { sensorId } = useParams();
  const [sensor, setSensor] = useState(null);
  const [radiationData, setRadiationData] = useState([]);
  const [forecast, setForecast] = useState(null);
  const [loading, setLoading] = useState(true);
  const [forecastLoading, setForecastLoading] = useState(false);
  const [error, setError] = useState(null);

  const [alpha, setAlpha] = useState(0.5);
  const [beta, setBeta] = useState(0.3);
  const [forecastSteps, setForecastSteps] = useState(1);

  const { t } = useTranslation();
  const token = localStorage.getItem("accessToken");

  useEffect(() => {
    const fetchSensorData = async () => {
      setLoading(true);
      setError(null);

      try {
        const [sensorRes, radiationRes] = await Promise.all([
          fetch(`http://localhost:8000/api/sensors/${sensorId}/`, {
            headers: { Authorization: `Bearer ${token}` },
          }),
          fetch(`http://localhost:8000/api/sensors/${sensorId}/radiation-data/`, {
            headers: { Authorization: `Bearer ${token}` },
          }),
        ]);

        if (!sensorRes.ok) {
          throw new Error(t("sensorDetails.errorLoadingSensor"));
        }
        if (!radiationRes.ok) {
          throw new Error(t("sensorDetails.errorLoadingRadiationData"));
        }

        const sensorJson = await sensorRes.json();
        const radiationJson = await radiationRes.json();

        setSensor(sensorJson);

        const validRadiationData = Array.isArray(radiationJson)
          ? radiationJson.filter(
              (item) =>
                item &&
                item.radiation_level !== null &&
                !isNaN(parseFloat(item.radiation_level))
            )
          : [];

        setRadiationData(validRadiationData);
        setForecast(null);
      } catch (e) {
        setError(e.message || t("sensorDetails.errorLoadingSensor"));
        setSensor(null);
        setRadiationData([]);
        setForecast(null);
      } finally {
        setLoading(false);
      }
    };

    if (sensorId && token) {
      fetchSensorData();
    } else {
      setError(t("sensorDetails.errorLoadingSensor"));
      setLoading(false);
    }
  }, [sensorId, token, t]);

  const doubleExponentialSmoothing = (data, alpha, beta, forecastSteps) => {
    if (data.length === 0) return null;

    let s = parseFloat(data[0].radiation_level);
    let b = 0;

    for (let t = 1; t < data.length; t++) {
      const c = parseFloat(data[t].radiation_level);
      const prevS = s;

      s = alpha * c + (1 - alpha) * (s + b);
      b = beta * (s - prevS) + (1 - beta) * b;
    }

    return s + forecastSteps * b;
  };

  const calculateLocalForecast = () => {
    setForecastLoading(true);
    setError(null);

    try {
      if (radiationData.length === 0) {
        setError(t("sensorDetails.noMeasurementsYet"));
        setForecast(null);
        setForecastLoading(false);
        return;
      }

      const forecastValue = doubleExponentialSmoothing(radiationData, alpha, beta, forecastSteps);

      if (forecastValue === null || isNaN(forecastValue)) {
        setError(t("sensorDetails.errorLoadingForecast"));
        setForecast(null);
      } else {
        setForecast({ predicted_radiation: forecastValue });
      }
    } catch (e) {
      setError(t("sensorDetails.errorLoadingForecast"));
      setForecast(null);
    } finally {
      setForecastLoading(false);
    }
  };

  if (loading) return <p className="loading">{t("sensorDetails.loadingSensorData")}</p>;
  if (error) return <p className="error">{error}</p>;
  if (!sensor) return <p className="error">{t("sensorDetails.sensorNotFound")}</p>;

  return (
    <div className="sensor-details">
      <h2>
        {t("sensorDetails.sensorDetailsTitle")} {sensor.sensor_name}
      </h2>

      <div className="sensor-info">
        <p><strong>{t("sensorDetails.status")}:</strong> {sensor.status}</p>
        <p><strong>{t("sensorDetails.lastUpdate")}:</strong> {sensor.last_update ? new Date(sensor.last_update).toLocaleString() : t("sensorDetails.unknown")}</p>
        <p><strong>{t("sensorDetails.location")}:</strong> {sensor.location?.city || t("sensorDetails.unknown")}</p>
      </div>

      <h3>{t("sensorDetails.radiationMeasurementsTitle")}</h3>
      {radiationData.length === 0 ? (
        <p>{t("sensorDetails.noMeasurementsYet")}</p>
      ) : (
        <table className="radiation-table">
          <thead>
            <tr>
              <th>{t("sensorDetails.measurementDate")}</th>
              <th>{t("sensorDetails.radiationLevel")}</th>
              <th>{t("sensorDetails.warning")}</th>
            </tr>
          </thead>
          <tbody>
            {radiationData.map((item) => (
              <tr key={item.id}>
                <td>{new Date(item.measured_at).toLocaleString()}</td>
                <td>{parseFloat(item.radiation_level).toFixed(2)}</td>
                <td>{item.alert_triggered ? t("sensorDetails.warningYes") : t("sensorDetails.warningNo")}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <h3>{t("sensorDetails.radiationForecast")}</h3>

      <div className="admin-controls">
        <label>
          α (alpha):
          <input type="number" step="0.01" min="0" max="1" value={alpha} onChange={(e) => setAlpha(parseFloat(e.target.value))} />
        </label>
        <label>
          β (beta):
          <input type="number" step="0.01" min="0" max="1" value={beta} onChange={(e) => setBeta(parseFloat(e.target.value))} />
        </label>
        <label>
          {t("forecastSteps")}:
          <input type="number" min="1" value={forecastSteps} onChange={(e) => setForecastSteps(parseInt(e.target.value))} />
        </label>
      </div>

      <button className="forecast-button" onClick={calculateLocalForecast} disabled={forecastLoading}>
        {forecastLoading ? t("sensorDetails.loading") : t("sensorDetails.getForecast")}
      </button>

      {forecast && (
        <div className="forecast-box">
          <p>
            <strong>{t("sensorDetails.expectedRadiationLevel")}:</strong> {Number(forecast.predicted_radiation).toFixed(2)} μSv/h
          </p>
        </div>
      )}
    </div>
  );
}

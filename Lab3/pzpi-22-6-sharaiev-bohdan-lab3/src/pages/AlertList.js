import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import '../styles/SensorList.css';

export default function AlertsList() {
  const { t, i18n } = useTranslation();

  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchAlerts = async () => {
      setLoading(true);
      setError(null);
      try {
        const token = localStorage.getItem('accessToken');
        const res = await fetch("http://localhost:8000/api/alerts/", {
          headers: {
            'Authorization': `Bearer ${token}`,
          }
        });
        if (!res.ok) throw new Error(t("alerts.fetch_error"));
        const data = await res.json();
        setAlerts(data);
      } catch (e) {
        setError(e.message);
      } finally {
        setLoading(false);
      }
    };

    fetchAlerts();

    const onLanguageChanged = () => {
      fetchAlerts();
    };

    i18n.on('languageChanged', onLanguageChanged);

    return () => {
      i18n.off('languageChanged', onLanguageChanged);
    };
  }, [i18n, t]); 

  const deleteAlert = async (id) => {
    if (!window.confirm(t("alerts.delete_confirm"))) return;

    try {
      const token = localStorage.getItem('accessToken');
      const res = await fetch(`http://localhost:8000/api/alerts/${id}/`, {
        method: "DELETE",
        headers: {
          'Authorization': `Bearer ${token}`,
        }
      });
      if (!res.ok) throw new Error(t("alerts.delete_error"));
      setAlerts((prev) => prev.filter((a) => a.id !== id));
    } catch (e) {
      alert(e.message);
    }
  };

  if (loading) return <p className="loadingMsg">{t("alerts.loading_alerts")}</p>;

  return (
    <div className="container">
      <h2 className="title">{t("alerts.alertsTitle")}</h2>

      {error && <p style={{ color: "red" }}>{error}</p>}

      <table className="table">
        <thead>
          <tr>
            <th>{t("alerts.sensor")}</th>
            <th>{t("alerts.message")}</th>
            <th>{t("alerts.level")}</th>
            <th>{t("alerts.time")}</th>
            <th>{t("alerts.status")}</th>
            <th>{t("alerts.actions")}</th>
          </tr>
        </thead>
        <tbody>
          {alerts.length === 0 ? (
            <tr>
              <td colSpan="6" className="emptyMsg" style={{ textAlign: "center" }}>
                {t("alerts.no_alerts")}
              </td>
            </tr>
          ) : (
            alerts.map((alert) => (
              <tr key={alert.id}>
                <td>{alert.sensor?.sensor_name || '–'}</td>
                <td>{alert.alert_message}</td>
                <td>{alert.alert_level}</td>
                <td>{new Date(alert.triggered_at).toLocaleString()}</td>
                <td className={alert.resolved ? "statusOnline" : "statusOffline"}>
                  {alert.resolved ? t("alerts.resolved") : t("alerts.active")}
                </td>
                <td className="actions">
                  <button onClick={() => deleteAlert(alert.id)}>{t("alerts.deleteButton")}</button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import '../styles/ReportList.css';

export default function ReportsList() {
  const [sensors, setDevices] = useState([]);
  const [reports, setReports] = useState([]);
  const [sensorId, setSensorId] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [loading, setLoading] = useState(false);
  const token = localStorage.getItem('accessToken');
  const { t } = useTranslation();

  useEffect(() => {
    fetchReports();
    fetchSensors();
  }, []);

  const fetchSensors = async () => {
    try {
      const res = await fetch('http://localhost:8000/api/sensors/', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!res.ok) throw new Error('Failed to fetch sensors');
      const data = await res.json();
      setDevices(data);
    } catch (error) {
      console.error('Error loading sensors:', error);
    }
  };

  const fetchReports = async () => {
    try {
      const res = await fetch('http://localhost:8000/api/reports/', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!res.ok) throw new Error('Failed to fetch');
      const data = await res.json();
      setReports(data);
    } catch (error) {
      console.error('Error loading reports:', error);
    }
  };

  const handleGenerate = async () => {
    if (!sensorId || !startDate || !endDate) return;
    setLoading(true);
    try {
      const res = await fetch(`http://localhost:8000/api/reports/generate/${sensorId}/${startDate}/${endDate}/`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!res.ok) throw new Error('Failed to generate report');
      await fetchReports();
    } catch (error) {
      console.error('Error generating report:', error);
    }
    setLoading(false);
  };

  const handleDelete = async (id) => {
    try {
      const res = await fetch(`http://localhost:8000/api/reports/${id}/`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!res.ok) throw new Error('Failed to delete');
      await fetchReports();
    } catch (error) {
      console.error('Error deleting report:', error);
    }
  };

  const handleDownload = async (reportId, reportName) => {
    try {
      const res = await fetch(`http://localhost:8000/api/reports/download/${reportId}/`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!res.ok) throw new Error('Failed to download');
      const blob = await res.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = reportName || 'report.pdf';
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Error downloading report:', error);
    }
  };

  return (
    <div className="location-container">
      <h2 className="location-title">{t('reports.userReports')}</h2>

      <div className="form" style={{ marginBottom: '20px', textAlign: 'center' }}>
        <select
          value={sensorId}
          onChange={(e) => setSensorId(e.target.value)}
          style={{ padding: '8px', marginRight: '10px', borderRadius: '4px', border: '1px solid #ccc' }}
        >
          <option value="">{t('reports.selectSensor')}</option>
          {sensors.map((device) => (
            <option key={device.id} value={device.id}>
              {device.sensor_name || device.name || `${t('reports.sensor')} ${device.id}`}
            </option>
          ))}
        </select>
        <input
          type="date"
          value={startDate}
          onChange={(e) => setStartDate(e.target.value)}
          style={{ padding: '8px', marginRight: '10px', borderRadius: '4px', border: '1px solid #ccc' }}
        />
        <input
          type="date"
          value={endDate}
          onChange={(e) => setEndDate(e.target.value)}
          style={{ padding: '8px', marginRight: '10px', borderRadius: '4px', border: '1px solid #ccc' }}
        />
        <button className="add-button" onClick={handleGenerate} disabled={loading}>
          {loading ? t('reports.generating') : t('reports.generateReport')}
        </button>
      </div>

      <table className="location-table">
        <thead>
          <tr>
            <th>{t('reports.reportName')}</th>
            <th>{t('reports.sensor')}</th>
            <th>{t('reports.actions')}</th>
          </tr>
        </thead>
        <tbody>
          {reports.length === 0 ? (
            <tr>
              <td colSpan="3" style={{ textAlign: 'center', padding: '15px', color: '#555' }}>
                {t('reports.noReportsFound')}
              </td>
            </tr>
          ) : (
            reports.map((report) => (
              <tr key={report.id}>
                <td>{report.report_name}</td>
                <td>{report.sensor.sensor_name || '---'}</td>
                <td className="actions">
                  <button onClick={() => handleDownload(report.id, report.report_name)}>
                    {t('reports.download')}
                  </button>
                  <button onClick={() => handleDelete(report.id)}>{t('reports.delete')}</button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

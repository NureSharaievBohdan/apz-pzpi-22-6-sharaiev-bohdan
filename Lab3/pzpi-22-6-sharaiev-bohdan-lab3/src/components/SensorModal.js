import React, { useEffect, useState } from 'react';
import '../styles/SensorModal.css';

export default function SensorModal({ isOpen, onClose, onSubmit, initialData, locations = [] }) {
  const [sensorName, setSensorName] = useState('');
  const [description, setDescription] = useState('');
  const [status, setStatus] = useState('offline');
  const [location_id, setLocationId] = useState('');

  useEffect(() => {
    if (initialData) {
      setSensorName(initialData.sensor_name || '');
      setDescription(initialData.description || '');
      setStatus(initialData.status || 'offline');
      setLocationId(initialData.location?.id || '');
    } else {
      setSensorName('');
      setDescription('');
      setStatus('offline');
      setLocationId('');
    }
  }, [initialData, isOpen]);

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    const payload = {
      sensor_name: sensorName,
      description,
      status,
      location_id: location_id || null,  
  ...(initialData?.id ? { id: initialData.id } : {}),
    };
    onSubmit(payload);
    onClose();
  };

  return (
    <div className="modal-overlay">
      <div className="modal">
        <h3>{initialData ? 'Редагування сенсора' : 'Додавання сенсора'}</h3>
        <form onSubmit={handleSubmit}>
          <label>
            Назва сенсора:
            <input
              type="text"
              value={sensorName}
              onChange={(e) => setSensorName(e.target.value)}
              required
            />
          </label>

          <label>
            Опис:
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={3}
            />
          </label>

          <label>
            Статус:
            <select value={status} onChange={(e) => setStatus(e.target.value)} required>
              <option value="online">online</option>
              <option value="offline">offline</option>
            </select>
          </label>

          <label>
            Локація:
            <select value={location_id} onChange={(e) => setLocationId(e.target.value)} required>
              <option value="">-- Оберіть локацію --</option>
              {locations.map((loc) => (
                <option key={loc.id} value={loc.id}>
                  {loc.city}
                </option>
              ))}
            </select>
          </label>

          <div className="modal-actions">
            <button type="submit">{initialData ? 'Зберегти' : 'Додати'}</button>
            <button type="button" onClick={onClose}>Скасувати</button>
          </div>
        </form>
      </div>
    </div>
  );
}

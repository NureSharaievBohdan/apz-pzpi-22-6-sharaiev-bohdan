import { useState, useEffect } from "react";
import "../styles/AdminDataBase.css";

export default function AdminDatabase() {
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [backups, setBackups] = useState([]);
  const [selectedBackup, setSelectedBackup] = useState("");

  const token = localStorage.getItem('accessToken');

  useEffect(() => {
    fetchBackups();
  }, []);

  const fetchBackups = async () => {
    try {
      const response = await fetch("http://localhost:8000/maintenance/backups/", {
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
      if (!response.ok) throw new Error("Не вдалося отримати список бекапів");

      const data = await response.json();
      if (data.status === "success" && Array.isArray(data.backups)) {
        setBackups(data.backups);
      } else {
        throw new Error("Невірний формат відповіді сервера");
      }
    } catch (err) {
      setError(err.message);
    }
  };

  const runMigrations = async () => {
    resetStatus();
    try {
      const res = await fetch("http://localhost:8000/maintenance/make-migrations/", {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
      const data = await res.json();
      res.ok ? setMessage(data.message) : setError(data.error || "Помилка міграції");
    } catch (err) {
      setError("Серверна помилка при міграції");
    }
  };

  const createBackup = async () => {
    resetStatus();
    try {
      const res = await fetch("http://localhost:8000/maintenance/create-backup/", {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
      const data = await res.json();
      res.ok ? setMessage(data.message) : setError(data.message);
      fetchBackups();
    } catch (err) {
      setError("Помилка при створенні бекапу");
    }
  };

  const restoreBackup = async (backupFile) => {
    resetStatus();
    
    if (!backupFile) {
      setError("Оберіть файл бекапу для відновлення.");
      return;
    }

    try {
      const res = await fetch("http://localhost:8000/maintenance/restore-backup/", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ backup_file: backupFile })
      });
      const data = await res.json();
      res.ok ? setMessage(data.message) : setError(data.message);
    } catch (err) {
      setError("Серверна помилка при відновленні бекапу");
    }
  };

  const resetStatus = () => {
    setMessage("");
    setError("");
  };

  return (
    <div className="container">
      <h1 className="title">Адміністрування БД</h1>

      <div style={{ textAlign: "center", marginBottom: "20px" }}>
        <button onClick={runMigrations} className="actions button">Виконати міграції</button>
        <button onClick={createBackup} className="actions button">Створити бекап</button>
      </div>

      <h2 className="title" style={{ fontSize: "1.4rem" }}>Доступні бекапи</h2>
      {backups.length === 0 ? (
        <p className="emptyMsg">Немає доступних бекапів</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>#</th>
              <th>Файл бекапу</th>
              <th>Дія</th>
            </tr>
          </thead>
          <tbody>
            {backups.map((file, index) => (
              <tr key={index}>
                <td>{index + 1}</td>
                <td>{file}</td>
                <td className="actions">
                  <button
                    onClick={() => {
                      setSelectedBackup(file);
                      restoreBackup(file);  
                    }}
                  >
                    Відновити
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {message && <p className="statusOnline">{message}</p>}
      {error && <p className="errorMsg">{error}</p>}
    </div>
  );
}

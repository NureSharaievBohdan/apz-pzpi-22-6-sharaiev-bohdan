import React from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';  
import '../styles/Header.css';

export default function Header() {
  const { pathname } = useLocation();
  const { t, i18n } = useTranslation();

  if (pathname === '/login' || pathname === '/register') return null;

  const isAdmin = pathname.startsWith('/admin');

  const changeLanguage = (lng) => {
    i18n.changeLanguage(lng);
  };

  return (
    <header className={`header ${isAdmin ? 'admin-header' : ''}`}>
      <div className="logo">
        {isAdmin ? t('header.logoAdmin') : t('header.logoUser')}
      </div>

      <nav className="nav">
        <div className="nav-links">
          {isAdmin ? (
            <>
              <NavLink to="/admin" className="nav-link">
                {t('header.navAdminSystem')}
              </NavLink>
              <NavLink to="/admin/database" className="nav-link">
                {t('header.navAdminDatabase')}
              </NavLink>
            </>
          ) : (
            <>
              <NavLink to="/sensors" className="nav-link">
                {t('header.navDevices')}
              </NavLink>
              <NavLink to="/reports" className="nav-link">
                {t('header.navReports')}
              </NavLink>
              <NavLink to="/locations" className="nav-link">
                {t('header.navLocations')}
              </NavLink>
              <NavLink to="/profile" className="nav-link">
                {t('header.navProfile')}
              </NavLink>
              <NavLink to="/alerts" className="nav-link">
                {t('header.navAlerts')}
              </NavLink>
            </>
          )}
        </div>

        <div className="language-switcher">
          <button onClick={() => changeLanguage('ua')} className="lang-button">{t('header.langUA')}</button>
          <button onClick={() => changeLanguage('en')} className="lang-button">{t('header.langEN')}</button>
        </div>
      </nav>

    </header>
  );
}

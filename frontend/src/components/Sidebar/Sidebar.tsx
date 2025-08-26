import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import './Sidebar.scss';
import { items } from './SideItems';
import { useAuth } from '../../context/AuthContext';

interface SidebarItemProps {
    to: string;
    icon?: React.ReactNode; // эмодзи/иконка :)
    children: string;
}

const SidebarItem: React.FC<SidebarItemProps> = ({ to, icon, children }) => {
    return (
        <li className="sidebar__item">
            <NavLink
                to={to}
                end={to === '/'} // чтобы "/" не подсвечивался на всех маршрутах
                className={({ isActive }) =>
                    `sidebar__link${isActive ? ' sidebar__link--active' : ''}`
                }
            >
                {icon && <span className="sidebar__icon">{icon}</span>}
                {children}
            </NavLink>
        </li>
    );
};

export const Sidebar: React.FC = () => {
    const { isAuthenticated, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <aside className="sidebar">
            <div className="sidebar__header">
                <h2 className="sidebar__title">Блог</h2>
            </div>

            <nav className="sidebar__nav">
                <ul className="sidebar__list">
                    {items.map(({ to, icon, label }) => (
                        <SidebarItem key={to} to={to} icon={icon}>
                            {label}
                        </SidebarItem>
                    ))}
                </ul>

                {isAuthenticated ? (
                    <button onClick={handleLogout} className="sidebar__logout">
                        Выйти
                    </button>
                ) : (
                    <NavLink to="/login" className="sidebar__link">
                        Войти
                    </NavLink>
                )}
            </nav>
        </aside>
    );
};

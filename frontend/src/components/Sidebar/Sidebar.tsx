import React from 'react'
import {Link, useLocation} from 'react-router-dom'
import './Sidebar.scss'

export const Sidebar: React.FC = () => {
    const location = useLocation()

    const isActive = (path: string) => {
        return location.pathname === path
    }

    return (
        <aside className="sidebar">
            <div className="sidebar__header">
                <h2 className="sidebar__title">Блог</h2>
            </div>

            <nav className="sidebar__nav">
                <ul className="sidebar__list">
                    <li className="sidebar__item">
                        <Link
                            to="/"
                            className={`sidebar__link ${isActive('/') ? 'sidebar__link--active' : ''}`}
                        >
                            <span className="sidebar__icon">📝</span>
                            Все посты
                        </Link>
                    </li>
                    <li className="sidebar__item">
                        <Link
                            to="/create"
                            className={`sidebar__link ${isActive('/create') ? 'sidebar__link--active' : ''}`}
                        >
                            <span className="sidebar__icon">➕</span>
                            Создать пост
                        </Link>
                    </li>
                    <li className="sidebar__item">
                        <Link
                            to="/my-posts"
                            className={`sidebar__link ${isActive('/my-posts') ? 'sidebar__link--active' : ''}`}
                        >
                            <span className="sidebar__icon">👤</span>
                            Мои посты
                        </Link>
                    </li>
                    <li className="sidebar__item">
                        <Link
                            to="/deleted"
                            className={`sidebar__link ${isActive('/deleted') ? 'sidebar__link--active' : ''}`}
                        >
                            <span className="sidebar__icon">🗑️</span>
                            Удаленные посты
                        </Link>
                    </li>
                </ul>
            </nav>
        </aside>
    )
}

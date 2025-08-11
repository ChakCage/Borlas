import React from 'react'
import { Link, useLocation } from 'react-router-dom'
import './Sidebar.scss'

interface SidebarItemProps {
    to: string
    icon?: React.ReactNode // эмодзи :)
    children: string
    isActive: boolean
}

const SidebarItem: React.FC<SidebarItemProps> = ({ to, icon, children, isActive }) => {
    return (
        // li дает возможность не рендерить страницу при переходе, взяли из 'react-router-dom'
        <li className="sidebar__item">
            <Link to={to} className={`sidebar__link ${isActive ? 'sidebar__link--active' : ''}`}> {/*В линке лежит to = раздел, класснейм линк, если нет линка - вернет на главную*/}
                {icon && <span className="sidebar__icon">{icon}</span>} {/*далее показывает иконку, если нет иконки - не рендерит span (0x0)*/}
                {children} {/*children - string*/}
            </Link>
        </li>
    )
}

export const Sidebar: React.FC = () => {
    const location = useLocation()

    const items = [
        { to: '/', icon: '📝', label: 'Все посты' },
        { to: '/create', icon: '➕', label: 'Создать пост' },
        { to: '/my-posts', icon: '👤', label: 'Мои посты' },
        { to: '/deleted', icon: '🗑️', label: 'Удаленные посты' },
        { to: '/testView', icon: null, label: 'Тестовый экран' },
    ]

    return (
        <aside className="sidebar">
            <div className="sidebar__header">
                <h2 className="sidebar__title">Блог</h2>
            </div>

            <nav className="sidebar__nav">
                <ul className="sidebar__list">
                    {/* мапом берем масив пунктов, ul - список */}
                    {items.map(({ to, icon, label }) => (
                        <SidebarItem key={to} to={to} icon={icon} isActive={location.pathname === to}>
                            {label}
                        </SidebarItem>
                    ))}
                </ul>
            </nav>
        </aside>
    )
}

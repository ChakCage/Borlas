import { FaRegFileAlt, FaPlus, FaUser, FaTrash } from 'react-icons/fa';

export const items = [
    { to: '/', icon: <FaRegFileAlt />, label: 'Все посты' },
    { to: '/create', icon: <FaPlus />, label: 'Создать пост' },
    { to: '/my-posts', icon: <FaUser />, label: 'Мои посты' },
    { to: '/deleted', icon: <FaTrash />, label: 'Удаленные посты' },
    { to: '/testView', icon: null, label: 'Тестовый экран' },
]

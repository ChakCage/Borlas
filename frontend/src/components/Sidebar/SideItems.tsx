import DynamicFeedIcon from '@mui/icons-material/DynamicFeed';
import AddIcon from '@mui/icons-material/Add';
import PersonIcon from '@mui/icons-material/Person';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import DeleteIcon from '@mui/icons-material/Delete';
import LockOpenIcon from '@mui/icons-material/LockOpen';

export const items = [
    { to: '/auth', icon: <LockOpenIcon />, label: 'Авторизация' },
    { to: '/', icon: <DynamicFeedIcon />, label: 'Все посты' },
    { to: '/create', icon: <AddIcon />, label: 'Создать пост' },
    { to: '/my-posts', icon: <PersonIcon />, label: 'Мои посты' },
    { to: '/my-profile', icon: <AccountCircleIcon />, label: 'Мой профиль' },
    { to: '/deleted', icon: <DeleteIcon />, label: 'Удаленные посты' },
    // { to: '/testView', icon: null, label: 'Тестовый экран' },
];

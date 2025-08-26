import DynamicFeedIcon from '@mui/icons-material/DynamicFeed';
import AddIcon from '@mui/icons-material/Add';
import PersonIcon from '@mui/icons-material/Person';
import DeleteIcon from '@mui/icons-material/Delete';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';

export const items = [
    { to: '/posts',   icon: <DynamicFeedIcon />,   label: 'Все посты' },
    { to: '/create',  icon: <AddIcon />,           label: 'Создать пост' },
    { to: '/my-posts',icon: <PersonIcon />,        label: 'Мои посты' },
    { to: '/deleted', icon: <DeleteIcon />,        label: 'Удалённые посты' },
    { to: '/profile', icon: <AccountCircleIcon />, label: 'Профиль' },   // ✅ новое
];

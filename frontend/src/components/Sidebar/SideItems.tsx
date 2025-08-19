import DynamicFeedIcon from '@mui/icons-material/DynamicFeed';
import AddIcon from '@mui/icons-material/Add';
import PersonIcon from '@mui/icons-material/Person';
import DeleteIcon from '@mui/icons-material/Delete';

export const items = [
    { to: '/', icon: <DynamicFeedIcon />, label: 'Все посты' },
    { to: '/create', icon: <AddIcon />, label: 'Создать пост' },
    { to: '/my-posts', icon: <PersonIcon />, label: 'Мои посты' },
    { to: '/deleted', icon: <DeleteIcon />, label: 'Удаленные посты' },
    // { to: '/testView', icon: null, label: 'Тестовый экран' },


];

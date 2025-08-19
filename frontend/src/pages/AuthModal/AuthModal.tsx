import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {useAuth} from "../../context/AuthContext";
import { login } from '../../api/axios';
import './AuthModal.scss';
import {usersApi} from "../../api/usersApi";

const AuthModal: React.FC = () => {
    const [isLoginMode, setIsLoginMode] = useState(true);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [birthDate, setBirthDate] = useState('');
    const [gender, setGender] = useState<string | null>(null);
    const [errors, setErrors] = useState<Record<string, string>>({});
    const [isLoading, setIsLoading] = useState(false);
    const { login: authLogin } = useAuth();
    const navigate = useNavigate();

    // Автоформатирование и валидация даты рождения
    useEffect(() => {
        if (birthDate.length === 2 || birthDate.length === 5) {
            setBirthDate(prev => prev + '.');
        }

        // Проверка при изменении значения
        if (birthDate.length === 10) {
            validateBirthDate();
        }
    }, [birthDate]);

    // Функция для проверки корректности даты
    const validateBirthDate = (): boolean => {
        if (!birthDate) return true;

        const dateParts = birthDate.split('.');
        if (dateParts.length !== 3) {
            setErrors(prev => ({ ...prev, birthDate: 'Неверный формат даты' }));
            return false;
        }

        const [day, month, year] = dateParts;
        const dayNum = parseInt(day, 10);
        const monthNum = parseInt(month, 10);
        const yearNum = parseInt(year, 10);

        // Проверка на числа
        if (isNaN(dayNum) || isNaN(monthNum) || isNaN(yearNum)) {
            setErrors(prev => ({ ...prev, birthDate: 'Дата должна содержать только цифры' }));
            return false;
        }

        // Проверка года (от 1900 до текущего года)
        const currentYear = new Date().getFullYear();
        if (yearNum < 1900 || yearNum > currentYear) {
            setErrors(prev => ({ ...prev, birthDate: `Год должен быть между 1900 и ${currentYear}` }));
            return false;
        }

        // Проверка месяца
        if (monthNum < 1 || monthNum > 12) {
            setErrors(prev => ({ ...prev, birthDate: 'Месяц должен быть от 01 до 12' }));
            return false;
        }

        // Проверка дня
        const daysInMonth = new Date(yearNum, monthNum, 0).getDate();
        if (dayNum < 1 || dayNum > daysInMonth) {
            setErrors(prev => ({ ...prev, birthDate: `В этом месяце должно быть от 01 до ${daysInMonth} дней` }));
            return false;
        }

        // Проверка на будущую дату
        const birthDateObj = new Date(yearNum, monthNum - 1, dayNum);
        const today = new Date();
        today.setHours(0, 0, 0, 0); // Сбрасываем время для точного сравнения
        if (birthDateObj > today) {
            setErrors(prev => ({ ...prev, birthDate: 'Дата рождения не может быть в будущем' }));
            return false;
        }

        // Проверка минимального возраста (12 лет)
        const minAgeDate = new Date();
        minAgeDate.setFullYear(minAgeDate.getFullYear() - 12);
        minAgeDate.setHours(0, 0, 0, 0);
        if (birthDateObj > minAgeDate) {
            setErrors(prev => ({ ...prev, birthDate: 'Минимальный возраст - 12 лет' }));
            return false;
        }

        // Если все проверки пройдены, очищаем ошибку
        setErrors(prev => {
            const newErrors = { ...prev };
            delete newErrors.birthDate;
            return newErrors;
        });

        return true;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        const newErrors: Record<string, string> = {};

        // Валидация email
        if (!email) {
            newErrors.email = 'Введите email';
        } else if (!/\S+@\S+\.\S+/.test(email)) {
            newErrors.email = 'Некорректный email';
        }

        // Валидация пароля
        if (!password) {
            newErrors.password = 'Введите пароль';
        } else if (password.length < 6) {
            newErrors.password = 'Пароль должен быть не менее 6 символов';
        }

        // Дополнительные проверки для регистрации
        if (!isLoginMode) {
            if (password !== confirmPassword) {
                newErrors.confirmPassword = 'Пароли не совпадают';
            }

            // Проверка даты рождения, если она указана
            if (birthDate && birthDate.length > 0) {
                const isValidDate = validateBirthDate();
                if (!isValidDate) {
                    // Ошибка уже установлена в validateBirthDate
                    newErrors.birthDate = errors.birthDate || 'Некорректная дата рождения';
                }
            }
        }

        // Если есть ошибки - показываем их и прерываем отправку
        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            setIsLoading(false);
            return;
        }

        // Здесь будет вызов API для авторизации/регистрации
        try {
            if (isLoginMode) {
                // Реальная авторизация
                const response = await login(email, password);
                authLogin(response.accessToken, response.refreshToken);
                navigate('/posts'); // Перенаправление после успешного входа
            } else {
                // Регистрация - здесь нужно добавить вызов API регистрации

                const userData = {
                    email,
                    password,
                    username: email.split('@')[0], // Генерируем username из email
                    bio: null,
                    avatarUrl: null,
                    birthDate: birthDate || null,
                    gender: gender || null
                };

                await usersApi.createUser(userData);

                // После успешной регистрации автоматически входим
                const response = await login(email, password);
                authLogin(response.accessToken, response.refreshToken);
                navigate('/posts');
                console.log('Регистрация:', userData);

            }
        } catch (error: any) {
            setErrors({ submit: error.response?.data?.message || 'Ошибка авторизации' });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="auth-modal-overlay">
            <div className="auth-modal">
                <h2>{isLoginMode ? 'Вход' : 'Регистрация'}</h2>

                <form onSubmit={handleSubmit} noValidate>
                    <div className="form-group">
                        <label>Email</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            placeholder="Введите email"
                            className={errors.email ? 'error' : ''}
                        />
                        {errors.email && <span className="error-message">{errors.email}</span>}
                    </div>

                    <div className="form-group">
                        <label>Пароль</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Введите пароль"
                            className={errors.password ? 'error' : ''}
                        />
                        {errors.password && <span className="error-message">{errors.password}</span>}
                    </div>

                    {!isLoginMode && (
                        <>
                            <div className="form-group">
                                <label>Подтвердите пароль</label>
                                <input
                                    type="password"
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    placeholder="Повторите пароль"
                                    className={errors.confirmPassword ? 'error' : ''}
                                />
                                {errors.confirmPassword && (
                                    <span className="error-message">{errors.confirmPassword}</span>
                                )}
                            </div>

                            <div className="form-group">
                                <label>Дата рождения (дд.мм.гггг)</label>
                                <input
                                    type="text"
                                    value={birthDate}
                                    onChange={(e) => {
                                        // Разрешаем только цифры и точки
                                        const value = e.target.value.replace(/[^\d.]/g, '');
                                        // Ограничиваем длину
                                        if (value.length <= 10) {
                                            setBirthDate(value);
                                        }
                                    }}
                                    placeholder="дд.мм.гггг"
                                    maxLength={10}
                                    className={errors.birthDate ? 'error' : ''}
                                />
                                <div className="date-input-hint">Например: 15.05.1990</div>
                                {errors.birthDate && (
                                    <span className="error-message">{errors.birthDate}</span>
                                )}
                            </div>

                            <div className="form-group">
                                <label>Пол (необязательно)</label>
                                <div className="gender-options">
                                    <label className="gender-option">
                                        <input
                                            type="radio"
                                            name="gender"
                                            checked={gender === 'MALE'}
                                            onChange={() => setGender('MALE')}
                                        />
                                        <span>Мужской</span>
                                    </label>
                                    <label className="gender-option">
                                        <input
                                            type="radio"
                                            name="gender"
                                            checked={gender === 'FEMALE'}
                                            onChange={() => setGender('FEMALE')}
                                        />
                                        <span>Женский</span>
                                    </label>
                                    <label className="gender-option">
                                        <input
                                            type="radio"
                                            name="gender"
                                            checked={gender === null}
                                            onChange={() => setGender(null)}
                                        />
                                        <span>Не указывать</span>
                                    </label>
                                </div>
                            </div>
                        </>
                    )}
                    {errors.submit && (
                        <div className="error-message submit-error">{errors.submit}</div>
                    )}
                    <div className="form-actions">
                        <button
                            type="submit"
                            className="primary-btn"
                            disabled={isLoading}>
                            {isLoading ? 'Загрузка...' : isLoginMode ? 'Войти' : 'Зарегистрироваться'}
                        </button>
                    </div>
                </form>

                <div className="auth-switch">
                    <p>
                        {isLoginMode ? 'Нет аккаунта?' : 'Уже есть аккаунт?'}
                        <button
                            type="button"
                            className="switch-btn"
                            onClick={() => {
                                setIsLoginMode(!isLoginMode);
                                setErrors({});
                            }}>
                            {isLoginMode ? ' Зарегистрироваться' : ' Войти'}
                        </button>
                    </p>
                </div>
            </div>
        </div>
    );
};

export default AuthModal;

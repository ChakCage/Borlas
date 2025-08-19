import React, { createContext, useContext, useState, useEffect } from 'react';
import { getAccessToken, clearTokens, setTokens } from '../api/axios';

interface AuthContextType {
    isAuthenticated: boolean;
    login: (token: string, refreshToken?: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(!!getAccessToken());

    useEffect(() => {
        setIsAuthenticated(!!getAccessToken());
    }, []);

    const login = (token: string, refreshToken?: string) => {
        setTokens(token, refreshToken);
        setIsAuthenticated(true);
    };

    const logout = () => {
        clearTokens();
        setIsAuthenticated(false);
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
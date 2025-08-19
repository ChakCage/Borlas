import React from 'react';
import { Navigate } from 'react-router-dom';
import { getAccessToken } from '../../api/axios';

interface ProtectedRouteProps {
    children: React.ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
    const token = getAccessToken();

    return token ? <>{children}</> : <Navigate to="/login" replace />;
};

export default ProtectedRoute;
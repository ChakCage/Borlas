import React from 'react'
import './TextButton.scss'
import {TextButtonTheme} from "../../types/BtnThemeEnum"

interface TextButtonProps {
    text: string
    onClick?: () => void
    theme?: TextButtonTheme
    isDisabled?: boolean
    className?: string;
}

export const TextButton: React.FC<TextButtonProps> = (props) => {

    const {isDisabled, text, onClick, theme} = props

    return (
        <button className={"text-button text-button_" + theme}
            onClick={onClick}
            disabled={isDisabled}
        >
            {text}
        </button>
    )
}

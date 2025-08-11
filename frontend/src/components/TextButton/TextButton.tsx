import React from 'react'
import './TextButton.scss'
import {TextButtonTheme} from "../../types/BtnThemeEnum"

interface TextButtonProps {
    text: string
    onClick: () => void
    theme?: TextButtonTheme
    isDisabled?: boolean
}

export const TextButton: React.FC<TextButtonProps> = (props) => {

    const {isDisabled, text, onClick, theme} = props

    return (
        <button className={"text-button"}
            onClick={onClick}
            disabled={isDisabled}
        >
            {text}
        </button>
    )
}

import React from 'react'
import './Button.scss'
import {ButtonTheme} from "../../types/BtnThemeEnum"

interface ButtonProps {
    text: string
    onClick: () => void
    theme?: ButtonTheme
    isDisabled?: boolean
}

export const Button: React.FC<ButtonProps> = ({
                                                  text,
                                                  onClick,
                                                  theme = ButtonTheme.Primary,
                                                  isDisabled
                                              }) => {


    return (
        <button className={"button-posts button-posts_" + theme}
                onClick={onClick}
                disabled={isDisabled}
        >{text}</button>)
}

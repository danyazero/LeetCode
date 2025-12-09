import { useState, type ReactNode } from "react";

export interface InputProps {
  prefix: ReactNode;
  placeholder: string;
  value: string;
  setValue(value: string): void;
}

export const Input = (props: InputProps) => {
  const [isFocused, setIsFocused] = useState(false);

  return (
    <div
      className={
        "flex flex-row gap-3 p-4 rounded-full w-full items-center transition-all duration-200 " +
        (isFocused ? "bg-white shadow-md" : "bg-foreground")
      }
    >
      {props.prefix}
      <input
        className={
          "text-xl font-light outline-none placeholder:text-gray w-full"
        }
        onFocus={() => setIsFocused(true)}
        onBlur={() => setIsFocused(false)}
        placeholder={props.placeholder}
        value={props.value}
        onChange={(event) => props.setValue(event.target.value)}
      />
    </div>
  );
};

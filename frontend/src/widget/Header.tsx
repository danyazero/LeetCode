import { Link } from "react-router";
import { keycloakContext } from "@/features/KeycloakWrapper";
import { UserAvatar } from "@/shared/UserAvatar";
import type { ReactNode } from "react";

interface HeaderProps {
  children?: ReactNode;
  className?: string;
}

export const Header = ({ children, className }: HeaderProps) => {
  return (
    <div className={`flex flex-row py-5 px-6 gap-4 items-center justify-between border-b border-border/60 bg-card ${className}`}>
      <div className="flex flex-row gap-8 items-center">
        <Link to="/" className="text-2xl font-bold tracking-tight hover:opacity-80 transition-opacity">
          ucode
        </Link>
        <div className="flex items-center gap-4">
          {children}
        </div>
      </div>
      
      <div className="flex items-center gap-4">
        {keycloakContext.authenticated ? (
          <>
            <UserAvatar username={keycloakContext.idTokenParsed?.preferred_username} />
            <button
              onClick={() => keycloakContext.logout()}
              className="text-sm font-medium hover:cursor-pointer hover:text-muted-foreground transition-colors"
            >
              Logout
            </button>
          </>
        ) : (
          <>
            <button
              onClick={() => keycloakContext.login()}
              className="text-sm font-medium hover:cursor-pointer hover:text-muted-foreground transition-colors"
            >
              Sign in
            </button>
            <button
              onClick={() => keycloakContext.register()}
              className="text-sm font-medium bg-primary text-primary-foreground px-4 py-2 rounded-md hover:bg-primary/90 transition-colors"
            >
              Sign up
            </button>
          </>
        )}
      </div>
    </div>
  );
};

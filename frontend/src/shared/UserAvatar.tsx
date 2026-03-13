import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

export interface UserAvatarProps {
  src?: string;
  username: string;
}

export function UserAvatar({ src, username }: UserAvatarProps) {
  const fallbackLetters = username
    ? username.slice(0, 2).toUpperCase()
    : "?";

  return (
    <Avatar>
      {src && <AvatarImage src={src} alt={username} />}
      <AvatarFallback className="bg-primary/10 text-primary font-medium">
        {fallbackLetters}
      </AvatarFallback>
    </Avatar>
  );
}

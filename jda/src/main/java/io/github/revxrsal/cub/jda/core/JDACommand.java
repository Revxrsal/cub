package io.github.revxrsal.cub.jda.core;

import io.github.revxrsal.cub.core.BaseCommandHandler;
import io.github.revxrsal.cub.core.BaseHandledCommand;
import io.github.revxrsal.cub.jda.JDACommandSubject;
import io.github.revxrsal.cub.jda.annotation.GuildPermission;
import io.github.revxrsal.cub.jda.annotation.OwnerOnly;
import io.github.revxrsal.cub.jda.annotation.RolePermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;

class JDACommand extends BaseHandledCommand {

    public JDACommand(BaseCommandHandler handler, Object instance, @Nullable BaseHandledCommand parent, @Nullable AnnotatedElement ae) {
        super(handler, instance, parent, ae);
    }

    @Override protected void setProperties() {
        RolePermission rp = getAnnotation(RolePermission.class);
        if (rp != null) {
            permission = sender -> {
                List<Role> roles = ((JDACommandSubject) sender).asMember().getRoles();
                for (long id : rp.ids())
                    if (roles.stream().anyMatch(r -> r.getIdLong() == id))
                        return true;
                for (String name : rp.names())
                    if (roles.stream().anyMatch(r -> r.getName().equalsIgnoreCase(name)))
                        return true;
                return false;
            };
            return;
        }
        GuildPermission gp = getAnnotation(GuildPermission.class);
        if (gp != null) {
            permission = sender -> {
                Member member = ((JDACommandSubject) sender).asMember();
                for (Permission permission : gp.value()) {
                    if (!member.hasPermission(permission)) return false;
                }
                return true;
            };
            return;
        }
        OwnerOnly oo = getAnnotation(OwnerOnly.class);
        if (oo != null) {
            permission = sender -> ((JDACommandSubject) sender).asMember().isOwner();
        }
    }

    @Override protected BaseHandledCommand newCommand(BaseCommandHandler handler, Object o, BaseHandledCommand parent, Class<?> innerClass) {
        return new JDACommand(handler, o, parent, innerClass);
    }

    @Override protected BaseHandledCommand newCommand(BaseCommandHandler handler, Object o, BaseHandledCommand parent, Method method) {
        return new JDACommand(handler, o, parent, method);
    }
}

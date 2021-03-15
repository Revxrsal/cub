
# Cub
[![](https://jitpack.io/v/ReflxctionDev/cub.svg)](https://jitpack.io/#ReflxctionDev/cub)

![A nice little cub](https://i.imgur.com/AjsOvv4.jpg)

Cub is an **annotation-based**, **powerful**, **highly customizable** commands framework, made to *greatly* reduce the boilerplate of writing commands, parsing and validating input, creating tab  completions and flags, and many other burdensome things we have to go through all the time when we write a command.

Cub also aims to allow developers to focus more on writing code than all the other tasks that we aren't supposed to spend a lot of time doing. It reduces the hassle to reinvent the wheels, doing the search and all the calculations, and makes complicated functionality simple through annotations.

## There are many commands frameworks out there, why should I use Cub?
Glad you asked!

- **Cub is pretty small**: with its core less than 70 KB. It's universal, and does not depend on any other library to function
- **Cub is easy**: Creating a simple, executable command has never been easier
- **Cub is extendable**: This one is very unique in comparison to other commands frameworks:
    - You can create your own annotations and give them their own functionality
    - You can handle all exceptions and catch invalid usages. No messages are hard-coded
    - You can implement your own "command results", which are values returned by command methods, and handle them in your own style
    - You can register parameter resolvers. Write once, use everywhere.
    - You can create your own parameter validators, which can also allow you to check for custom annotations
    - You can create your own command exceptions and handle them
    - You can create your own conditions and validate them against your own annotations.

- **Cub is powerful**: Cub focuses on letting you write only the important parts only, and handles the rest by itself:
    -  Extensive support for asynchronisity and *CompletionStage*s
    - Allows string parameters to accept quoted text (to include spaces or empty values)
    - Platform-independent. You can use Cub in **Bukkit**, **JDA** or even your **command-line program**!
    - Allows for flexible functionality for parameters:
        - [Flag parameters](https://github.com/ReflxctionDev/cub/blob/master/common/src/main/java/io/github/revxrsal/cub/annotation/Flag.java)
        - [Switch parameters](https://github.com/ReflxctionDev/cub/blob/master/common/src/main/java/io/github/revxrsal/cub/annotation/Switch.java)
        - [Optional parameters](https://github.com/ReflxctionDev/cub/blob/master/common/src/main/java/io/github/revxrsal/cub/annotation/Optional.java)
        - [Parameters with default values](https://github.com/ReflxctionDev/cub/blob/master/common/src/main/java/io/github/revxrsal/cub/annotation/Default.java)
    - Easily create [subcommands](https://github.com/ReflxctionDev/cub/blob/master/common/src/main/java/io/github/revxrsal/cub/annotation/Subcommand.java), or [catch invalid usages](https://github.com/ReflxctionDev/cub/blob/master/common/src/main/java/io/github/revxrsal/cub/annotation/CatchInvalid.java).
    - Built-in [dependency injection](https://github.com/ReflxctionDev/cub/blob/master/common/src/main/java/io/github/revxrsal/cub/annotation/Dependency.java).
    - Instead of having to register lambdas for conditions or parameter resolvers, you can avoid the [callback hell](http://callbackhell.com/) and create resolver objects, which define methods to resolve values.
        - [ConditionEvaluator](https://github.com/ReflxctionDev/cub/blob/master/common/src/main/java/io/github/revxrsal/cub/annotation/ConditionEvaluator.java)
        - [ValueResolver](https://github.com/ReflxctionDev/cub/blob/master/common/src/main/java/io/github/revxrsal/cub/annotation/ValueResolver.java)
        - [ContextResolver](https://github.com/ReflxctionDev/cub/blob/master/common/src/main/java/io/github/revxrsal/cub/annotation/ContextResolver.java)
        - [TabResolver](https://github.com/ReflxctionDev/cub/blob/master/bukkit/src/main/java/io/github/revxrsal/cub/bukkit/annotation/TabResolver.java) for Bukkit
        - [ContextResolverFactories](https://github.com/ReflxctionDev/cub/blob/master/common/src/main/java/io/github/revxrsal/cub/ContextResolverFactory.java) and [ValueResolverFactories](https://github.com/ReflxctionDev/cub/blob/master/common/src/main/java/io/github/revxrsal/cub/ValueResolverFactory.java) for creating dedicated resolvers for special types. Even more potential!
    - (Bukkit) No need to explicitly define completions per method as they can be fetched from the command parameters automatically.
- **Cub is flexible**: The API in cub has extensive documentation, and is very simple and easy to hook into. With the ability to integrate custom annotations, custom validators and custom resolvers, there is so much that you can do with this library!
- **Cub is fast**: Cub uses the Java 7-introduced MethodHandles API, which is a faster alternative to the classic Java reflection, hence better performance. Cub also caches everything for later use, hence no need for any lookups after registering.

# Wiki
The wiki is an exhaustive source of information for using Cub.

Take a look [here](https://github.com/ReflxctionDev/cub/wiki).

# Example
```java
@Command("ticket")
@Description("Handle tickets")
public class TicketCommands {

    @Dependency
    private TicketFactory ticketFactory;

    @Subcommand("new")
    @Cooldown(value = 10, unit = TimeUnit.MINUTES)
    @Description("Open a new ticket")
    public String openTicket(Player sender, String content) {
        Ticket ticket = ticketFactory.createTicket(sender.getUniqueId(), content);
        return "&aSuccessfully created ticket with ID &e" + ticket.getId() + "&a.";
    }

    @Subcommand("close")
    @TabCompletion("@tickets")
    @Description("Close the ticket")
    @TicketModeratorOnly
    public String closeTicket(@OpenedTicket Ticket ticket) {
        ticketFactory.closeTicket(ticket);
        return "&aSuccessfully closed ticket &e" + ticket.getId() + "&a.";
    }

    @Subcommand("assign")
    @Description("Assign the ticket to another moderator")
    @TabCompletion("@tickets @ticketAdmins")
    @TicketModeratorOnly
    public String setAssignee(@OpenedTicket Ticket ticket, @TicketModerator Player delegateTo) {
        ticket.setAssignee(delegateTo);
        return "&aSuccessfully delegated ticket &e" + ticket.getId() + "&a to &e" + delegateTo.getName() + "&a.";
    }
}
```

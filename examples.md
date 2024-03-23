```java
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
        Player player = event.getPlayer();

        TextComponent textComponent = new TextComponent();
        textComponent.setText(" ");
        textComponent.setFont("minecraft:default");

        BaseComponent[] head1 = getHead(UUID.fromString("d30e61cb-f6d6-4941-b30c-b2c6adce9254"));
        BaseComponent[] head2 = getHead(UUID.fromString("c2013a02-a45b-411c-b79a-006ee3ec8295"));
        BaseComponent[] head3 = getHead(UUID.fromString("f0453a4c-abd1-4fc9-9f1d-6bab7f685096"));
        BaseComponent[] head4 = getHead(UUID.fromString("84e96f90-203e-449e-9af7-95a383d6ff1a"));
        BaseComponent[] head5 = getHead(player);

        BaseComponent[] combinedHeads = new ComponentBuilder()
                .append(head1)
                .append(textComponent)
                .append(head2)
                .append(textComponent)
                .append(head3)
                .append(textComponent)
                .append(head4)
                .append(textComponent)
                .append(head5)
                .create();

        player.spigot().sendMessage(combinedHeads);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, combinedHeads);
        System.out.println(combinedHeads.toString());

    }
```
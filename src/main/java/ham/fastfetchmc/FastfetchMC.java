package ham.fastfetchmc;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader; 
import java.io.IOException;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.nio.file.Files;

public class FastfetchMC implements ModInitializer {
	public static final String MOD_ID = "fastfetch-mc";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("fastfetch").executes(context -> {
				Path configPath = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("fastfetch-config.jsonc");
				if (Files.notExists(configPath)) {
					generateConfigFile(configPath);
				}
				LOGGER.info("Path: {}", configPath.toString());
				String[] command = {"fastfetch", "-c", configPath.toString()};
				String message = executeShellCommand(command);


				context.getSource().sendFeedback(() -> Text.literal(message), false);
				return 1;
			}));
		});

		LOGGER.info("Loaded fastfetchmc");
	}

	public static String executeShellCommand(String[] command) {
		// Attempts to run the command.
        try {
            Process process = Runtime.getRuntime().exec(command);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } 
		// Log the error, I am going to cry
		catch (IOException e) {
			LOGGER.error("Failed to execute command: {}", e.getMessage());
            return null;
        }
    }

	public static void generateConfigFile(Path configPath) {

		try {
			FileWriter writer = new FileWriter(configPath.toString());
			writer.write("Hello World");
			writer.close();
			LOGGER.info("Successfully created fastfetch config file at: {}", configPath.toString());
		  } 
		  catch (IOException e) {
			LOGGER.error("Error creating fastfetch config. {}", e.getMessage());
		  }
	}
}
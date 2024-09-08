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
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Gets the path for the config file.
		Path configPath = net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("fastfetch-config.jsonc");
		// If it doesn't exist, I'll make it exist...
		if (Files.notExists(configPath)) {
			generateConfigFile(configPath);
		}
		//
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("fastfetch").executes(context -> {
				LOGGER.info("Path: {}", configPath.toString());
				String[] command = {"fastfetch", "-c", configPath.toString()};
				String message = executeShellCommand(command);


				context.getSource().sendFeedback(() -> Text.literal(message), false);
				return 1;
			}));
		});
		// The mod loaded, I was not scared it would crash...
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
		// Will create the config and write the default config.
		try {
			FileWriter writer = new FileWriter(configPath.toString());
			writer.write(createJsonString());
			writer.close();
			LOGGER.info("Successfully created fastfetch config file at: {}", configPath.toString());
		  } 
		  // Logs the error so I can find it or something idk
		  catch (IOException e) {
			LOGGER.error("Error creating fastfetch config. {}", e.getMessage());
		  }
	}

	public static String createJsonString() {
		// Please help, there has to be a better way of doing this. But I am too tired right now to care. It works though :3
		String config = new StringBuilder()
		.append("{\n")
		.append("    \"$schema\": \"https://github.com/fastfetch-cli/fastfetch/raw/dev/doc/json_schema.json\",\n")
		.append("    \"logo\": {\n")
		.append("        \"type\": \"none\"\n")
		.append("    },\n")
		.append("    \"modules\": [\n")
		.append("        \"title\",\n")
		.append("        \"os\",\n")
		.append("        \"kernel\",\n")
		.append("        \"uptime\",\n")
		.append("        \"cpu\",\n")
		.append("        \"gpu\",\n")
		.append("        {\n")
		.append("            \"type\": \"memory\",\n")
		.append("            \"key\": \"RAM\"\n")
		.append("        }\n")
		.append("    ]\n")
		.append("}")
		.toString();

		return config;
	}
}
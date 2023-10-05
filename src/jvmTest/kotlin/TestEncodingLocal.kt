import com.aallam.kotoken.loader.LocalPbeLoader
import okio.FileSystem

class TestEncodingLocal : AbstractEncoding(
    modelName = "gpt-3.5-turbo-16k",
    loader = LocalPbeLoader(fileSystem = FileSystem.RESOURCES),
)
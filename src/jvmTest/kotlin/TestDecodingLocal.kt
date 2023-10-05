import com.aallam.kotoken.loader.LocalPbeLoader
import okio.FileSystem

class TestDecodingLocal : AbstractDecoding(
    loader = LocalPbeLoader(fileSystem = FileSystem.RESOURCES),
)

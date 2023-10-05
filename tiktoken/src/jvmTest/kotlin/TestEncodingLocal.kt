import com.aallam.kotoken.loader.LocalPbeLoader
import okio.FileSystem

class TestEncodingLocal : AbstractEncoding(
    loader = LocalPbeLoader(fileSystem = FileSystem.RESOURCES),
)

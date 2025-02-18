import java.util.UUID;

public class UuidIdService implements IdService
{
    @Override
    public String generateId() {return UUID.randomUUID().toString();}
}

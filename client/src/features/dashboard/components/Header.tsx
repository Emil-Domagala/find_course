type Props = { title: string; subtitle: string; rightElement?: React.ReactNode };

const Header = ({ title, subtitle, rightElement }: Props) => {
  return (
    <header className="mb-7 flex justify-between items-center">
      <div>
        <h1 className="text-3xl font-bold text-white-50">{title}</h1>
        <p className="text-sm text-gray-500 mt-1">{subtitle}</p>
      </div>
      {rightElement && <div>{rightElement}</div>}
    </header>
  );
};

export default Header;

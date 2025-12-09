import { Container } from "@/shared/Container";
import { Input } from "@/shared/Input";
import { ProblemItem } from "@/shared/ProblemItem";
import { useState } from "react";
import { GoSearch } from "react-icons/go";

export const ProblemsPage = () => {
  const [search, setSearch] = useState("");

  return (
    <div className="flex flex-row justify-center w-full ">
      <div className="flex flex-col max-w-4xl w-full gap-6 mt-8">
        <p className="text-4xl text-black">Problems</p>
        <Input
          prefix={<GoSearch size={24} color="var(--color-gray)" />}
          placeholder="Search"
          value={search}
          setValue={setSearch}
        />

        <Container>
          <ProblemItem
            id={1}
            title="Simple problem"
            difficulty="Easy"
            isCompleted={true}
          />
          <ProblemItem
            id={2}
            title="Digits"
            difficulty="Easy"
            isCompleted={true}
          />
          <ProblemItem
            id={3}
            title="Match's model"
            difficulty="Medium"
            isCompleted={false}
          />
          <ProblemItem
            id={4}
            title="Two circles"
            difficulty="Easy"
            isCompleted={true}
          />
          <ProblemItem
            id={5}
            title="Two factors"
            difficulty="Medium"
            isCompleted={false}
          />
          <ProblemItem
            id={6}
            title="The Vouchers"
            difficulty="Hard"
            isCompleted={false}
          />
          <ProblemItem
            id={7}
            title="Roman numerals"
            difficulty="Easy"
            isCompleted={false}
          />
        </Container>
      </div>
    </div>
  );
};
